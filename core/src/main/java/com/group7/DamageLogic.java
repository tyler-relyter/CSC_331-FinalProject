package com.group7;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class DamageLogic {
    private final Player player;

    // checks the logic between the players attack box bounds and the enemy bounds to see if it overlaps.
    // if it does, damage the enemy and set the health. If it dies remove it from the game screen and add
    // a kill count to the player.
    public DamageLogic(Player player, Array<GameEntity> enemies) {
        this.player = player;// player class that is on game screen

        //checks every enemy that's added to the enemies game entity array
        for (GameEntity e: enemies) {
            update(e);
        }
    }

    public void update(GameEntity enemy) {
        boolean isPlayerAttacking = player.isAttacking();
        Rectangle playerAttackBounds = player.getPlayerAttackBounds();
        Rectangle enemyBounds = enemy.getBounds();
        //checks if the player's attack bounds are within the enemies area on the map
        if (playerAttackBounds.overlaps(enemyBounds) && isPlayerAttacking){
            enemy.modifyHealth(-(player.getDamage()));
            if (enemy.getHealth() <= 0){ //if the enemy dies
                enemy.setIsAlive(false);
                enemy.dispose();
                checkEnemyDeath(enemy); // checks if the enemy is dead or not and increments kill count of player.
            }
        }
    }

    // checks to see if the player has killed an enemy and updates the kill counter if it does.
    private void checkEnemyDeath(GameEntity enemy){
        if (!enemy.getIsAlive() && !enemy.getDeathHandled()){
            enemy.setDeathHandled(true);
            player.incrementKillCounter();
            System.out.println(player.getKillCount());
        }
    }
}


