package com.group7;

import com.badlogic.gdx.math.Rectangle;

public class DamageLogic {

    private final Player player;
    private final GameEntity enemy;


    public DamageLogic(Player player, GameEntity enemy) {
        this.player = player;
        this.enemy = enemy;
    }


    public void check() {
        boolean isPlayerAttacking = player.isAttacking();
        Rectangle playerAttackBounds = player.getPlayerAttackBounds();
        Rectangle enemyBounds = enemy.getBounds();

        if (playerAttackBounds.overlaps(enemyBounds) && isPlayerAttacking){
            enemy.modifyHealth(-(player.getDamage()));
            if (enemy.getHealth() <= 0){
                enemy.dispose();
            }
        }
    }




}


