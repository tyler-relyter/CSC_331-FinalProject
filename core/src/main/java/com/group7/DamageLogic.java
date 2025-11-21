package com.group7;

import com.badlogic.gdx.math.Rectangle;

public class DamageLogic {
    private final Player player;
    private final GameEntity enemy;

    // checks the logic between the players attack box bounds and the enemy bounds to see if it overlaps.
    // if it does, damage the enemy and set the health. If it dies remove it from the game screen and add
    // a kill count to the player.
    public DamageLogic(Player player, GameEntity enemy) {
        this.player = player; // player class that is on game screen
        this.enemy = enemy; // single enemy on game screen **This needs to be changed to array to check all enemies**
        // when more enemies are added this will have to be modified to add the enemy arrays to the logic.
    }
    public void update() {
        boolean isPlayerAttacking = player.isAttacking();
        Rectangle playerAttackBounds = player.getPlayerAttackBounds();
        Rectangle enemyBounds = enemy.getBounds();

        // for multiple enemies there should be a loop that iterates over each enemy in the array and performs this.
        // Should remove the enemy from the array also when it's disposed.
        if (playerAttackBounds.overlaps(enemyBounds) && isPlayerAttacking){
            enemy.modifyHealth(-(player.getDamage()));
            if (enemy.getHealth() <= 0){
                enemy.setIsAlive(false);
                enemy.dispose();
                checkEnemyDeath();
            }
        }
    }
    private void checkEnemyDeath(){
        if (!enemy.getIsAlive() && !enemy.getDeathHandled()){
            enemy.setDeathHandled(true);
            player.incrementKillCounter();
            System.out.println(player.getKillCount());
        }
    }
}


