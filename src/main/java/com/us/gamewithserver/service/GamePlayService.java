package com.us.gamewithserver.service;

import com.us.gamewithserver.model.*;
import com.us.gamewithserver.payload.GamePlayRequests.requests.*;
import com.us.gamewithserver.payload.GamePlayRequests.responses.*;
import com.us.gamewithserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

@Service
public class GamePlayService {
    private final GameProgressRepository gameProgressRepository;
    private final PlayerRankingRepository playerRankingRepository;
    private final UserRepository userRepository;
    private final SoloStatsRepository soloStatsRepository;
    private final MultiPlayerMatchHistoryRepository multiPlayerMatchHistoryRepository;
    private final SoloMatchHistoryRepository soloMatchHistoryRepository;
    private final TeamMatchHistoryRepository teamMatchHistoryRepository;

    @Autowired
    public GamePlayService(UserRepository userRepository,
                           GameProgressRepository gamePlayRepository,
                           PlayerRankingRepository playerRankingRepository,
                           SoloStatsRepository soloStatsRepository,
                           MultiPlayerMatchHistoryRepository multiPlayerMatchHistoryRepository,
                           SoloMatchHistoryRepository soloMatchHistoryRepository,
                           TeamMatchHistoryRepository teamMatchHistoryRepository) {
        this.gameProgressRepository = gamePlayRepository;
        this.playerRankingRepository = playerRankingRepository;
        this.userRepository = userRepository;
        this.soloStatsRepository = soloStatsRepository;
        this.multiPlayerMatchHistoryRepository = multiPlayerMatchHistoryRepository;
        this.soloMatchHistoryRepository = soloMatchHistoryRepository;
        this.teamMatchHistoryRepository = teamMatchHistoryRepository;
    }

    public ResponseEntity<?> updateCurrentPosition(UpdatePlayerCurrentPositionRequest updatePlayerCurrentPositionRequest) {
        String userId = updatePlayerCurrentPositionRequest.getUserId();
        Integer sceneIndex = updatePlayerCurrentPositionRequest.getSceneIndex();
        String currentPosition = updatePlayerCurrentPositionRequest.getPlayerPosition();
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.setSceneIndex(sceneIndex);
            gameProgress.setCurrentPosition(currentPosition);
            gameProgress.setLastUpdated(new Date(System.currentTimeMillis()));
            this.gameProgressRepository.save(gameProgress);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game progress not found");
        }
    }

    public ResponseEntity<?> updateCheckpointLocation(UpdateCheckpointLocationRequest updateCheckpointLocationRequest) {
        String userId = updateCheckpointLocationRequest.getUserId();
        Integer sceneIndex = updateCheckpointLocationRequest.getSceneIndex();
        String checkpointLocation = updateCheckpointLocationRequest.getCheckpointLocation();
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.setSceneIndex(sceneIndex);
            gameProgress.setCheckpointLocation(checkpointLocation);
            gameProgress.setLastUpdated(new Date(System.currentTimeMillis()));
            this.gameProgressRepository.save(gameProgress);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game progress not found");
        }
    }

    public ResponseEntity<?> updateScenePoints(UpdateScenePointsRequest updateScenePointsRequest) {
        String userId = updateScenePointsRequest.getUserId();
        int sceneIndex = updateScenePointsRequest.getSceneIndex();
        float points = updateScenePointsRequest.getScenePoint();
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.getScenePoints()[sceneIndex - 1] = points;
            gameProgress.setLastUpdated(new Date(System.currentTimeMillis()));
            this.gameProgressRepository.save(gameProgress);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game progress not found");
        }
    }

    public ResponseEntity<?> updateDeathCount(UpdateDeathCountRequest updateDeathCountRequest) {
        String userId = updateDeathCountRequest.getUserId();
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.setDeathCount(gameProgress.getDeathCount() + 1);
            gameProgress.setLastUpdated(new Date(System.currentTimeMillis()));
            this.gameProgressRepository.save(gameProgress);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game progress not found");
        }
    }

    public ArrayList<GameProgress> getAllGameProgresses() {
        return (ArrayList<GameProgress>)this.gameProgressRepository.findAll();
    }

    public ResponseEntity<?> rankPlayers() {
        AtomicBoolean gameProgressIsFound = new AtomicBoolean(true);
        AtomicBoolean playerRankingIsFound = new AtomicBoolean(true);
        ArrayList<GameProgress> allUserGameProgresses = this.getAllGameProgresses();
        allUserGameProgresses.forEach(userGameProgress -> {
            if (this.playerRankingRepository.existsByUserId(userGameProgress.getUserId())) {
                Optional<PlayerRanking> optionalPlayerRanking = this.playerRankingRepository.findByUserId(userGameProgress.getUserId());
                if (optionalPlayerRanking.isPresent()) {
                    PlayerRanking playerRanking = optionalPlayerRanking.get();
                    float finalPoints = 0;
                    float finalFinishTime = 0;
                    float[] scenePoints = userGameProgress.getScenePoints();
                    float[] sceneFinishTimes = userGameProgress.getSceneFinishTimes();
                    for (float scenePoint : scenePoints) {
                        finalPoints += scenePoint;
                    }
                    playerRanking.setFinalPoints(finalPoints);
                    playerRanking.setTotalDeaths(userGameProgress.getDeathCount());
                    for (float finishTime: sceneFinishTimes) {
                        finalFinishTime += finishTime;
                    }
                    playerRanking.setFinishTime(finalFinishTime);
                    this.playerRankingRepository.save(playerRanking);
                }
                else {
                    gameProgressIsFound.set(false);
                }
            }
            else {
                playerRankingIsFound.set(false);
            }
        });
        if (!playerRankingIsFound.get()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player ranking not found");
        }
        if (!gameProgressIsFound.get()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game progress not found");
        }
        ArrayList<PlayerRanking> allPlayerRankings = (ArrayList<PlayerRanking>) this.playerRankingRepository.findAll();
        Collections.sort(allPlayerRankings);
        Collections.reverse(allPlayerRankings);
        AtomicBoolean userFound = new AtomicBoolean(true);

        ArrayList<String> usernameList = new ArrayList<>();
        ArrayList<Float> finalFinishTimeList = new ArrayList<>();
        ArrayList<Integer> totalDeathCountList = new ArrayList<>();
        ArrayList<Float> finalPointsList = new ArrayList<>();
        AtomicInteger rank = new AtomicInteger();
        allPlayerRankings.forEach(playerRanking -> {
            Optional<User> optionalUser = this.userRepository.findById(playerRanking.getUserId());
            if (optionalUser.isPresent()) {
                rank.getAndIncrement();
                playerRanking.setRank(rank.get());
                usernameList.add(optionalUser.get().getUsername());
                finalFinishTimeList.add(playerRanking.getFinishTime());
                totalDeathCountList.add(playerRanking.getTotalDeaths());
                finalPointsList.add(playerRanking.getFinalPoints());
            }
            else {
                userFound.set(false);
            }
        });
        if (!userFound.get()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok().body(new PlayerRankingResponse(usernameList, finalFinishTimeList, totalDeathCountList, finalPointsList));
    }

    public ResponseEntity<?> newGame(NewGameRequest newGameRequest) {
        String userId = newGameRequest.getUserId();
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        Optional<PlayerRanking> optionalPlayerRanking = this.playerRankingRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent() && optionalPlayerRanking.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            PlayerRanking playerRanking = optionalPlayerRanking.get();
            gameProgress.setCheckpointLocation("");
            gameProgress.setHealthStatus(1);
            gameProgress.setDeathCount(0);
            gameProgress.setSceneIndex(1);
            gameProgress.setScenePoints(new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0});
            gameProgress.setSceneFinishTimes(new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0});
            gameProgress.setCurrentPosition("");
            gameProgress.setLastUpdated(null);
            this.gameProgressRepository.save(gameProgress);

            playerRanking.setFinalPoints(0);
            playerRanking.setRank(null);
            playerRanking.setTotalDeaths(0);
            playerRanking.setFinishTime(0);
            this.playerRankingRepository.save(playerRanking);

            return ResponseEntity.ok().body("Reset player's game progress and rank successfully");
        }
        else if (optionalGameProgress.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game progress not found");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player ranking not found");
        }
    }

    public ResponseEntity<?> gameContinue(GameContinueRequest gameContinueRequest) {
        String userId = gameContinueRequest.getUserId();
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            return ResponseEntity.ok().body(new GameContinueResponse(gameProgress.getSceneIndex(), gameProgress.getCurrentPosition()));
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game progress not found");
        }
    }

    public ResponseEntity<?> updateSceneFinishTime(UpdateSceneFinishTimeRequest updateSceneFinishTimeRequest) {
        String userId = updateSceneFinishTimeRequest.getUserId();
        int sceneIndex = updateSceneFinishTimeRequest.getSceneIndex();
        float sceneFinishTime = updateSceneFinishTimeRequest.getSceneFinishTime();
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.getSceneFinishTimes()[sceneIndex - 1] = sceneFinishTime;
            this.gameProgressRepository.save(gameProgress);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game progress not found");
    }

    public ResponseEntity<?> getAllSoloStats() {
        return ResponseEntity.ok().body(new GetAllSoloStatsResponse(new ArrayList<>(soloStatsRepository.findAll())));
    }

    public ResponseEntity<?> getMultiPlayerMatchHistory() {
        ArrayList<MultiPlayerMatchHistory> multiPlayerMatchHistoryList = new ArrayList<>(multiPlayerMatchHistoryRepository.findAll());
        return ResponseEntity.ok().body(new GetMultiPlayerMatchHistoryResponse(multiPlayerMatchHistoryList));
    }

    public ResponseEntity<?> getSoloMatchHistoryByUsername(GetSoloMatchHistoryByUsernameRequest getSoloMatchHistoryByUsernameRequest) {
        String filteredUsername = getSoloMatchHistoryByUsernameRequest.getUsername().strip();
        if (filteredUsername.contains("\u200B")) {
            filteredUsername = filteredUsername.substring(0, filteredUsername.indexOf("\u200B"));
        }
        ArrayList<SoloMatchHistory> soloMatchHistoryList = this.soloMatchHistoryRepository.findAllByUsername(filteredUsername);
        if (!soloMatchHistoryList.isEmpty()) {
            GetSoloMatchHistoryByUsernameResponse response = new GetSoloMatchHistoryByUsernameResponse(soloMatchHistoryList);
            return ResponseEntity.ok().body(response);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solo match history not found");
        }
    }

    public ResponseEntity<?> getTeamMatchHistoryByTeamName(GetTeamMatchHistoryByTeamNameRequest getTeamMatchHistoryByTeamNameRequest) {
        String filteredTeamName = getTeamMatchHistoryByTeamNameRequest.getTeamName().strip();
        if (filteredTeamName.contains("\u200B")) {
            filteredTeamName = filteredTeamName.substring(0, filteredTeamName.indexOf("\u200B"));
        }
        ArrayList<TeamMatchHistory> teamMatchHistoryList = this.teamMatchHistoryRepository.findAllByTeamName(filteredTeamName);
        if (!teamMatchHistoryList.isEmpty()) {
            return ResponseEntity.ok().body(new GetTeamMatchHistoryByTeamNameResponse(teamMatchHistoryList));
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team match history not found");
        }
    }

    public ResponseEntity<?> updateResult(SoloResultRequest res) {
        String winner = res.getWinner();
        String loser = res.getLoser();

        // find winner and loser in solo stats

        Optional<SoloStats> winnerStats = soloStatsRepository.findByUserName(winner);
        Optional<SoloStats> loserStats = soloStatsRepository.findByUserName(loser);

        // if null create a new solo stats for the winner and loser

        int newId1 = RandomGenerator.getDefault().nextInt(1000);
        int newId2 = RandomGenerator.getDefault().nextInt(1000);


        if (winnerStats.isEmpty()) {
            winnerStats = Optional.of(new SoloStats(UUID.randomUUID().toString() , "", 0f, 1000f, String.valueOf(newId1), winner));
        }

        if (loserStats.isEmpty()) {
            loserStats = Optional.of(new SoloStats(UUID.randomUUID().toString() , "", 0f, 1000f, String.valueOf(newId2), loser));
        }


        // update previous ratings
        winnerStats.get().setPreviousPoints(winnerStats.get().getTotalPoints());
        loserStats.get().setPreviousPoints(loserStats.get().getTotalPoints());

        // Before coding, determine the method for counting the value
        /*
        K factor = 32.000
        We determine 2 ratings Ra and Rb for players A and B
        We determine the expected score Ea and Eb for players A and B
        We determine the actual score Sa and Sb for players A and B
        We determine the new ratings R'a and R'b for players A and B

        Ea = 1 / (1 + 10^((Rb - Ra) / 400))
        Eb = 1 / (1 + 10^((Ra - Rb) / 400))

        Sa = 1 if player A wins, 0 if player B wins
        Sb = 1 if player B wins, 0 if player A wins

        R'a = Ra + K * (Sa - Ea)
        R'b = Rb + K * (Sb - Eb)

         */
        // code

        float k = 32.000f;
        float winnerRating = winnerStats.get().getTotalPoints();
        float loserRating = loserStats.get().getTotalPoints();

        float expectedWinner = 1 / (1 + (float)Math.pow(10, (loserRating - winnerRating) / 400));
        float expectedLoser = 1 / (1 + (float)Math.pow(10, (winnerRating - loserRating) / 400));

        float winnerScore = 1;
        float loserScore = 0;


        float newWinnerRating = winnerRating + k * (winnerScore - expectedWinner);
        float newLoserRating = loserRating + k * (loserScore - expectedLoser);

        winnerStats.get().setTotalPoints(newWinnerRating);
        loserStats.get().setTotalPoints(newLoserRating);

        // add each string of the column matchHistory of the winner and loser
        String winnerMatchHistory = winnerStats.get().getMatchHistory();
        String loserMatchHistory = loserStats.get().getMatchHistory();

        //L-1200.75,W-1500.5,L-1200,L-1000,W-1105.75,W-1300,W-1410.5,W-1500,L-1375.5,W-1500,W-1615.75,L-1540,L-1200.75,L-1100,W-1305.5

        winnerMatchHistory += "," + "W-" + winnerRating;
        loserMatchHistory += "," +  "L-" + loserRating;

        if (winnerMatchHistory.charAt(0)== ',') {
            winnerMatchHistory = winnerMatchHistory.substring(1);
        }

        if (loserMatchHistory.charAt(0)== ',') {
            loserMatchHistory = loserMatchHistory.substring(1);
        }

        winnerStats.get().setMatchHistory(winnerMatchHistory);
        loserStats.get().setMatchHistory(loserMatchHistory);

        soloStatsRepository.save(winnerStats.get());
        soloStatsRepository.save(loserStats.get());

        return ResponseEntity.ok().body("Result updated successfully");
    }

    public ResponseEntity<?> getTeamMatchHistoryByUsername(GetTeamMatchHistoryByUsernameRequest getTeamMatchHistoryByUsernameRequest) {
        String username = getTeamMatchHistoryByUsernameRequest.getUsername().strip();
        String filteredUsername = username.contains("\u200B") ? username.substring(0, username.indexOf("\u200B")) : username;
        List<TeamMatchHistory> allTeamMatchHistoryList = this.teamMatchHistoryRepository.findAll();
        ArrayList<TeamMatchHistory> returnedTeamMatchHistoryList = new ArrayList<>();
        allTeamMatchHistoryList.forEach(teamMatchHistory -> {
            TeamMember[] teamMembers = teamMatchHistory.getTeamMembers();
            for (TeamMember teamMember : teamMembers) {
                boolean oge = teamMember.getUsername().equalsIgnoreCase(filteredUsername);
                if (oge) {
                    returnedTeamMatchHistoryList.add(teamMatchHistory);
                    break;
                }
            }
        });
        if (!returnedTeamMatchHistoryList.isEmpty()) {
            return ResponseEntity.ok().body(new GetTeamMatchHistoryByUsernameResponse(returnedTeamMatchHistoryList));
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team match history not found");
        }
    }
}


