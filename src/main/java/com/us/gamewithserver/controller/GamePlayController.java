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
    public ResponseEntity<?> continueGame(@Valid @RequestBody GameContinueRequest gameContinueRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return this.gamePlayService.gameContinue(gameContinueRequest);
    }

    @PostMapping("/new-game")
    public ResponseEntity<?> newGame(@Valid @RequestBody NewGameRequest newGameRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return this.gamePlayService.newGame(newGameRequest);
    }

    @GetMapping("/get-all-single-stats")
    public ResponseEntity<?> playerRank() {
        return this.gamePlayService.rankPlayers();
    }

    @PostMapping("/update-death-count")
    public ResponseEntity<?> updateUserDeathCount(@Valid @RequestBody UpdateDeathCountRequest updateDeathCountRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return this.gamePlayService.updateDeathCount(updateDeathCountRequest);
    }

    @PostMapping("/update-player-position")
    public ResponseEntity<?> updateUserPosition(@Valid @RequestBody UpdatePlayerCurrentPositionRequest updatePlayerCurrentPositionRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return this.gamePlayService.updateCurrentPosition(updatePlayerCurrentPositionRequest);
    }

    @PostMapping("/update-checkpoint-location")
    public ResponseEntity<?> updateCheckpointLocation(@Valid @RequestBody UpdateCheckpointLocationRequest updateCheckpointLocationRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return this.gamePlayService.updateCheckpointLocation(updateCheckpointLocationRequest);
    }

    @PostMapping("/update-scene-finish-time")
    public ResponseEntity<?> updateSceneFinishTime(@Valid @RequestBody UpdateSceneFinishTimeRequest updateSceneFinishTimeRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return this.gamePlayService.updateSceneFinishTime(updateSceneFinishTimeRequest);
    }

    @PostMapping("/update-scene-points")
    public ResponseEntity<?> updateScenePoints(@Valid @RequestBody UpdateScenePointsRequest updateScenePointsRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return this.gamePlayService.updateScenePoints(updateScenePointsRequest);
    }

    @GetMapping("/get-all-solo-stats")
    public ResponseEntity<?> getAllSoloStats() {
        return this.gamePlayService.getAllSoloStats();
    }

    @GetMapping("/get-multi-player-match-history")
    public ResponseEntity<?> getMultiPlayerMatchHistory() {
        return this.gamePlayService.getMultiPlayerMatchHistory();
    }

    @PostMapping("/get-solo-match-history")
    public ResponseEntity<?> getSoloMatchHistory(@RequestBody GetSoloMatchHistoryByUsernameRequest getSoloMatchHistoryByUsernameRequest) {
        return this.gamePlayService.getSoloMatchHistoryByUsername(getSoloMatchHistoryByUsernameRequest);
    }

    @PostMapping("/get-team-match-history-by-team-name")
    public ResponseEntity<?> getTeamMatchHistoryByTeamName(@RequestBody GetTeamMatchHistoryByTeamNameRequest getTeamMatchHistoryByTeamNameRequest) {
        return this.gamePlayService.getTeamMatchHistoryByTeamName(getTeamMatchHistoryByTeamNameRequest);
    }

    @PostMapping("/get-team-match-history-by-user-name")
    public ResponseEntity<?> getTeamMatchHistoryByUsername(@RequestBody GetTeamMatchHistoryByUsernameRequest getTeamMatchHistoryByUsernameRequest) {
        return this.gamePlayService.getTeamMatchHistoryByUsername(getTeamMatchHistoryByUsernameRequest);
    }
}
