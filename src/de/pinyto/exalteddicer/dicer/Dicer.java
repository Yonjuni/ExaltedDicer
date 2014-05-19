package de.pinyto.exalteddicer.dicer;

/**
 * 
 * @author yonjuni
 * This class is used for dicing pool and damage.
 * If pool is used tens are counted twice. 
 * TODO change targetNumber for Special-Charms
 */

public class Dicer {
	
	
    public int targetNumber = 7;
    public int poolSize;
    
    public void setPoolSize(int poolSize){
        this.poolSize = poolSize;
    }

    public int getPoolSize(){
        return this.poolSize;
    }
    
    //For Special-Charms
    public void setTargetNumber(int targetNumber){
    	this.targetNumber = targetNumber;
    }

    public int[] rollDice(){
    	
        int[] dice = new int[poolSize];
        for (int i=0; i<dice.length; i++){
            dice[i] = (int)(Math.random() * 10) + 1;
            System.out.println("Ergebnis: " + dice[i]);
        }
        return dice;
    }
    
    public int evaluateDamage(){
    	int success = 0;
    	int ones = 0;
    	
        int[] dice = rollDice();

        for (int i=0; i<dice.length; i++){
        	
            if (dice[i] >= targetNumber) {
                success = success + 1;
            }else{
            if (dice[i] == 1) {
                ones = ones + 1;
            }
            
            }
        }

        if (success == 0 && ones > 0){
            return -1;
        }
       
        return success;
    }
    
    //Tens are counted twice
    public int evaluatePool(){

        int[] dice = rollDice();
    	int success = 0;
    	int ones = 0;
    	
        for (int i=0; i<dice.length; i++){
            if (dice[i] >= targetNumber && dice[i] != 10) {
                success = success + 1;
            }
            if (dice[i] == 1) {
                ones = ones + 1;
            }

            if (dice[i] == 10) {
                success = success + 2;
            }
        }

        if (success == 0 && ones > 0){
            return -1;
        }
        return success;
    }
    
}
