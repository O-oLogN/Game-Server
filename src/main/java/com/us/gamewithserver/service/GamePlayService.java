package com.us.gamewithserver.service;

import com.us.gamewithserver.model.GameProgress;
import com.us.gamewithserver.model.PlayerRanking;
import com.us.gamewithserver.model.Scene;
import com.us.gamewithserver.payload.GamePlayRequests.requests.*;
import com.us.gamewithserver.payload.GamePlayRequests.responses.GameContinueResponse;
import com.us.gamewithserver.repository.GameProgressRepository;
import com.us.gamewithserver.repository.PlayerRankingRepository;
import com.us.gamewithserver.repository.SceneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GamePlayService {
    private final GameProgressRepository gameProgressRepository;
    private final PlayerRankingRepository playerRankingRepository;
    private final SceneRepository sceneRepository;

    @Autowired
    public GamePlayService(GameProgressRepository gamePlayRepository, PlayerRankingRepository playerRankingRepository, SceneRepository sceneRepository) {
        this.gameProgressRepository = gamePlayRepository;
        this.playerRankingRepository = playerRankingRepository;
        this.sceneRepository = sceneRepository;
    }

    public ResponseEntity<?> updateCurrentPosition(UpdatePlayerCurrentPositionRequest updatePlayerCurrentPositionRequest) {
        String userId = updatePlayerCurrentPositionRequest.getUserId();
        String sceneId = updatePlayerCurrentPositionRequest.getSceneId();
        String currentPosition = updatePlayerCurrentPositionRequest.getPosition();
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.setSceneId(sceneId);
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
        String sceneId = updateCheckpointLocationRequest.getSceneId();
        String checkpointLocation = updateCheckpointLocationRequest.getCheckpointLocation();
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.setSceneId(sceneId);
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

    public ResponseEntity<?> rankPlayersAndGetRankByUserId(RankingPlayerRequest rankingPlayerRequest) {
        String userId = rankingPlayerRequest.getUserId();
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
        for (int i = 0; i < allPlayerRankings.size(); ++i) {
            allPlayerRankings.get(i).setRank(i + 1);
        }
        this.playerRankingRepository.saveAll(allPlayerRankings);

        Optional<PlayerRanking> optionalPlayerRanking = this.playerRankingRepository.findByUserId(userId);
        if (optionalPlayerRanking.isPresent()) {
           return ResponseEntity.ok().body(optionalPlayerRanking.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player ranking not found");
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
            Optional<Scene> optionalScene = this.sceneRepository.findByName("1stScene");
            if (optionalScene.isPresent())
            {
                gameProgress.setSceneId(optionalScene.get().getId());
            }
            else {
                throw new RuntimeException("Scene not found");
            }
            gameProgress.setScenePoints(new float[0]);
            gameProgress.setCurrentPosition("");
            gameProgress.setLastUpdated(null);
            this.gameProgressRepository.save(gameProgress);

            playerRanking.setFinalPoints(0);
            playerRanking.setTotalDeaths(0);
            playerRanking.setFinishTime(0);
            playerRanking.setRank(-1);
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
            Optional<Scene> optionalScene = this.sceneRepository.findById(gameProgress.getSceneId());
            if (optionalScene.isPresent()) {
                Scene scene = optionalScene.get();
                return ResponseEntity.ok( new GameContinueResponse(scene.getName(), gameProgress.getCurrentPosition()));
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scene not found");
            }
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
}


