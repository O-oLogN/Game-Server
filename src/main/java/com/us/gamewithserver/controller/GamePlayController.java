package com.us.gamewithserver.controller;

import com.us.gamewithserver.payload.GamePlayRequests.requests.*;
import com.us.gamewithserver.service.GamePlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/gameplay")
public class GamePlayController {
    private final GamePlayService gamePlayService;

    @Autowired
    public GamePlayController(GamePlayService gamePlayService) {
        this.gamePlayService = gamePlayService;
    }

    @PostMapping("/continue")
    public ResponseEntity<?> continueGame(@Valid @RequestBody String userId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        GameContinueRequest gameContinueRequest = new GameContinueRequest();
        gameContinueRequest.setUserId(userId);
        return this.gamePlayService.gameContinue(gameContinueRequest);
    }

    @PostMapping("/new-game")
    public ResponseEntity<?> newGame(@Valid @RequestBody String userId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setUserId(userId);
        return this.gamePlayService.newGame(newGameRequest);
    }

    @PostMapping("/rank")
    public ResponseEntity<?> playerRank(@Valid @RequestBody String userId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        RankingPlayerRequest rankingPlayerRequest = new RankingPlayerRequest();
        rankingPlayerRequest.setUserId(userId);
        return this.gamePlayService.rankPlayersAndGetRankByUserId(rankingPlayerRequest);
    }

    @PostMapping("/update-death-count")
    public ResponseEntity<?> updateUserDeathCount(@Valid @RequestBody String userId, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        UpdateDeathCountRequest updateDeathCountRequest = new UpdateDeathCountRequest();
        updateDeathCountRequest.setUserId(userId);
        return this.gamePlayService.updateDeathCount(updateDeathCountRequest);
    }

    @PostMapping("/update-player-position")
    public ResponseEntity<?> updateUserPosition(@Valid @RequestBody String userId, String sceneId, String position, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        UpdatePlayerCurrentPositionRequest updatePlayerCurrentPositionRequest = new UpdatePlayerCurrentPositionRequest();
        updatePlayerCurrentPositionRequest.setUserId(userId);
        updatePlayerCurrentPositionRequest.setSceneId(sceneId);
        updatePlayerCurrentPositionRequest.setPosition(position);
        return this.gamePlayService.updateCurrentPosition(updatePlayerCurrentPositionRequest);
    }

    @PostMapping("/update-checkpoint-location")
    public ResponseEntity<?> updateCheckpointLocation(@Valid @RequestBody String userId, String sceneId, String checkpointLocation, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        UpdateCheckpointLocationRequest updateCheckpointLocationRequest = new UpdateCheckpointLocationRequest();
        updateCheckpointLocationRequest.setUserId(userId);
        updateCheckpointLocationRequest.setSceneId(sceneId);
        updateCheckpointLocationRequest.setCheckpointLocation(checkpointLocation);
        return this.gamePlayService.updateCheckpointLocation(updateCheckpointLocationRequest);
    }

    @PostMapping("/update-scene-finish-time")
    public ResponseEntity<?> updateSceneFinishTime(@Valid @RequestBody String userId, int sceneIndex, float sceneFinishTime, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        UpdateSceneFinishTimeRequest updateSceneFinishTimeRequest = new UpdateSceneFinishTimeRequest();
        updateSceneFinishTimeRequest.setUserId(userId);
        updateSceneFinishTimeRequest.setSceneIndex(sceneIndex);
        updateSceneFinishTimeRequest.setSceneFinishTime(sceneFinishTime);
        return this.gamePlayService.updateSceneFinishTime(updateSceneFinishTimeRequest);
    }

    @PostMapping("/update-scene-points")
    public ResponseEntity<?> updateScenePoints(@Valid @RequestBody String userId, int sceneIndex, float scenePoints, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        UpdateScenePointsRequest updateScenePointsRequest = new UpdateScenePointsRequest();
        updateScenePointsRequest.setUserId(userId);
        updateScenePointsRequest.setSceneIndex(sceneIndex);
        updateScenePointsRequest.setScenePoint(scenePoints);
        return this.gamePlayService.updateScenePoints(updateScenePointsRequest);
    }
}
