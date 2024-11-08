package com.us.gamewithserver.service;

import com.us.gamewithserver.model.GameProgress;
import com.us.gamewithserver.model.PlayerRanking;
import com.us.gamewithserver.model.Scene;
import com.us.gamewithserver.model.User;
import com.us.gamewithserver.repository.GameProgressRepository;
import com.us.gamewithserver.repository.PlayerRankingRepository;
import com.us.gamewithserver.repository.SceneRepository;
import com.us.gamewithserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public GameProgress saveCurrentPosition(String userId, String sceneId, String currentPosition, Date lastUpdated) {
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.setSceneId(sceneId);
            gameProgress.setCurrentPosition(currentPosition);
            gameProgress.setLastUpdated(lastUpdated);
            return this.gameProgressRepository.save(gameProgress);
        }
        else {
            throw new RuntimeException("Game progress not found");
        }
    }

    public GameProgress saveCheckpointLocation(String userId, String sceneId, String checkpointLocation, Date lastUpdated) {
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.setSceneId(sceneId);
            gameProgress.setCheckpointLocation(checkpointLocation);
            gameProgress.setLastUpdated(lastUpdated);
            return this.gameProgressRepository.save(gameProgress);
        }
        else {
            throw new RuntimeException("Game progress not found");
        }
    }

    public GameProgress updateGameProgressScenePoints(String userId, String sceneId, int sceneIndex, float points, Date lastUpdated) {
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.setSceneId(sceneId);
            gameProgress.getScenePoints()[sceneIndex - 1] = points;
            gameProgress.setLastUpdated(lastUpdated);
            return this.gameProgressRepository.save(gameProgress);
        }
        else {
            throw new RuntimeException("Game progress not found");
        }
    }

    public GameProgress updateDeathCount(String userId, Date lastUpdated) {
        Optional<GameProgress> optionalGameProgress = this.gameProgressRepository.findByUserId(userId);
        if (optionalGameProgress.isPresent()) {
            GameProgress gameProgress = optionalGameProgress.get();
            gameProgress.setDeathCount(gameProgress.getDeathCount() + 1);
            gameProgress.setLastUpdated(lastUpdated);
            return this.gameProgressRepository.save(gameProgress);
        }
        else {
            throw new RuntimeException("Game progress not found");
        }
    }

    public ArrayList<GameProgress> getAllGameProgresses() {
        return (ArrayList<GameProgress>)this.gameProgressRepository.findAll();
    }

    public PlayerRanking rankPlayersAndGetRankByUserId(String userId) {
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
                    throw new RuntimeException("Player ranking not found");
                }
            }
            else {
                throw new RuntimeException("Game progress not found");
            }
        });
        ArrayList<PlayerRanking> allPlayerRankings = (ArrayList<PlayerRanking>) this.playerRankingRepository.findAll();
        Collections.sort(allPlayerRankings);
        for (int i = 0; i < allPlayerRankings.size(); ++i) {
            allPlayerRankings.get(i).setRank(i + 1);
        }
        this.playerRankingRepository.saveAll(allPlayerRankings);

        Optional<PlayerRanking> optionalPlayerRanking = this.playerRankingRepository.findByUserId(userId);
        if (optionalPlayerRanking.isPresent()) {
            return optionalPlayerRanking.get();
        }
        else {
            throw new RuntimeException("Player ranking not found");
        }
    }

    public void resetGameProgressAndRank(String userId) {
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
        }
        else if (optionalGameProgress.isEmpty()) {
            throw new RuntimeException("Game progress not found");
        }
        else {
            throw new RuntimeException("Player ranking not found");
        }
    }
}
