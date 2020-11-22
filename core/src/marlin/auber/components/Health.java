package marlin.auber.components;

import marlin.auber.common.Component;

/**
 * A component for Auber which tracks health.
 *
 * There should only be one of these in the scene.
 */
public class Health extends Component {

    /**
     * The maximum health the entity can have.
     */
    private float maxHealth = 100f;
    private float health;

    public Health() {
        this.health = this.maxHealth;
    }

    /**
     * Gets health of entity.
     * @return Remaining health of the entity.
     */
    public float getHealth() {
        return this.health;
    }

    /**
     * Reduces the health of the entity.
     * @param damage Damage to be dealt to the entity.
     */
    public void decreaseHealth(float damage) {
        this.health -= damage;
    }

    /**
     * Resets the health of the entity to the max health.
     */
    public void resetHealth() {
        this.health = this.maxHealth;
    }

    /**
     * Checks if game should end.
     * @return Returns whether or not the entity's health is 0 or below.
     */
    public boolean gameOver() {
        if(this.health <= 0f) {
            return true;
        }
        return false;
    }

    /**
     * Returns the max health of the entity.
     * @return Returns max health of the entity.
     */
    public float getMaxHealth() {
        return this.maxHealth;
    }
}
