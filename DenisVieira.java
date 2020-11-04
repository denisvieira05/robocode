package denisvieira;
import robocode.*;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * DenisVieira - a robot by (@denisvieira05)
 */


public class DenisVieira extends AdvancedRobot { 

    int moveDirection = 1; //which way to move
	
	public void run() {		
		setupRobotColors();
		setAdjustRadarForRobotTurn(true);//keep the radar still while we turn
		setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		
		turnRadarRightRadians(Double.POSITIVE_INFINITY);//keep turning radar right
	}
	
	public void onScannedRobot(ScannedRobotEvent scanEvent) { 
		Enemy enemy = new Enemy(scanEvent, moveDirection);
		
		lockTheRadar();
		slowerRobot();		

		if(isCloseEnough(scanEvent)) {
			enemy.tryKill();
		} else {
			enemy.findEnemies();
		}
	}
	
	private boolean isCloseEnough(ScannedRobotEvent scanEvent) {
		return scanEvent.getDistance() <= 150;
	}	
	
	private void lockTheRadar() {
        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
	}
	
	private void slowerRobot() {			
        if(Math.random()>.9){
            setMaxVelocity((12*Math.random())+12);//randomly change speed
        }
	}

	public void onHitWall(HitWallEvent e) {
        moveDirection=-moveDirection;//reverse direction upon hitting a wall
	}	
	
    /**
     * onWin:  Do a victory dance
     */
    public void onWin(WinEvent e) {
        for (int i = 0; i < 50; i++) {
            turnRight(30);
            turnLeft(30);
        }
    }
	
	private void setupRobotColors() {
		setColors(Color.black, Color.red, Color.green); // body, gun, radar
		setScanColor(Color.green);
		setBulletColor(Color.yellow);
	}
	
	public class Enemy {
		
		ScannedRobotEvent scanEvent; 
		int currentMoveDirection;
		double absBearing;
		double latVel;
		
	   public Enemy(ScannedRobotEvent scanEvent, int currentMoveDirection){
			this.scanEvent = scanEvent;
			this.currentMoveDirection = currentMoveDirection;
			this.absBearing = scanEvent.getBearingRadians()+getHeadingRadians();//enemies absolute bearing
        	this.latVel = scanEvent.getVelocity() * Math.sin(scanEvent.getHeadingRadians() - absBearing);//enemies later velocity
	   }
	
		public void tryKill() {
			turnGun(15);			
			turnPerpendicularlyToTheEnemy();
			goToTheEnemy();
            setFire(3); 
		}
		
		public void findEnemies() {
			turnGun(22);		
			searchEnemies();
			goToTheEnemy();
            setFire(3);
		}
		
		private void searchEnemies() {	
			setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing-getHeadingRadians()+latVel/getVelocity()));// dirigir para a localização futura prevista pelos inimigos
			
		}

		private void goToTheEnemy() {
    		setAhead((this.scanEvent.getDistance() - 140) * currentMoveDirection);//move forward		
		}
		
		private void turnPerpendicularlyToTheEnemy() {
	        setTurnLeft(-90-this.scanEvent.getBearing()); //turn perpendicular to the enemy
		}
		
		private void turnGun(int quantity) {
			double gunTurnAmt = getGunTurnQuantity(quantity);//amount to turn our gun
			
	        setTurnGunRightRadians(gunTurnAmt);//turn our gun
		}
		
		private double getGunTurnQuantity(int quantity) {
			return robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/quantity);//amount to turn our gun, lead just a little bit
		}
	}
}


