/*  Copyright (C) 2013 by Martin Kiessling, Tobias Roeding Inc. All rights reserved.
 *  Released under the terms of the GNU General Public License version 3 or later.
 *  
 *  Contributors:
 *  Martin Kiessling, Tobias Roeding - All
 */

package de.dhbw.td.core.waves;

import static playn.core.PlayN.json;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import playn.core.Json;
import playn.core.Json.Object;
import pythagoras.i.Point;
import de.dhbw.td.core.enemies.Enemy;
import de.dhbw.td.core.util.EFlavor;


/**
 * This class creates new Waves for each semester on demand.
 * 
 * @author Martin Kiessling, Tobias Roeding
 * 
 */
public class WaveControllerFactory {

	private static final int NUMBER_OF_WAVES = 12;
	private static final int NUMBER_OF_ATTRIBUTES = 3;
	private static final int UB_ENEMY_TYPES = 6;
	private static final EFlavor[] enemyTypeArray = EFlavor.values();
	
	private int currentSemester = 0;
	private Point[] waypoints;
	private int enemyCount;
		
	/**
	 * Returns upcoming wave controller and deletes it from the wave controller
	 * 
	 * @param jsonString location of the waves.json as String
	 * @param waypoints Queue of waypoints for current level
	 * @return next WaveController
	 * @see WaveController
	 */
	public WaveController constructWaveController(String jsonString, Point[] waypointArray) {
		return constructWaveController(json().parse(jsonString), waypointArray);
	}

	public WaveController constructWaveController(Object parsedJson, Point[] waypointArray) {
		this.waypoints = waypointArray;
		
		int[][] semester = new int[NUMBER_OF_WAVES][NUMBER_OF_ATTRIBUTES];

		this.enemyCount = parsedJson.getInt("enemyCount");
		Json.Array semesterArr = parsedJson.getArray("waves");

		for (int row = 0; row < NUMBER_OF_WAVES; row++) {
			Json.Array gridRow = semesterArr.getArray(row);

			for (int col = 0; col < NUMBER_OF_ATTRIBUTES; col++) {
				int val = gridRow.getInt(col);
				semester[row][col] = val;
			}
		}
		currentSemester++;
		return new WaveController(createWaves(semester));
	}

	/**
	 * Method creates all waves per WaveController and returns Wave-Queue.
	 * 
	 * @param semesters array containing [wave][attributes]
	 * @return Queue with waves per semester
	 * 
	 */
	private Queue<Wave> createWaves(int[][] semesters) {
		Random r = new Random();
		Queue<Wave> waves = new LinkedList<Wave>();
		for (int waveNumber = 0; waveNumber < NUMBER_OF_WAVES; waveNumber++) {
			List<Enemy> enemies = new LinkedList<Enemy>();
			for (int enemyNumber = 0; enemyNumber < enemyCount; enemyNumber++) {
				int maxHealth = semesters[waveNumber][0];
				double speed = semesters[waveNumber][1];
				int bounty = semesters[waveNumber][2];
				int next = r.nextInt(UB_ENEMY_TYPES);
				EFlavor enemyType = enemyTypeArray[next];
				enemies.add(new Enemy(maxHealth, speed, bounty, enemyType, waypoints));
			}
			Wave wave = new Wave(waveNumber, enemies);
			waves.add(wave);
		}
		return waves;
	}

	/**
	 * 
	 * @return currentSemester as integer
	 */
	public int getCurrentSemester() {
		return currentSemester;
	}

	/**
	 * 
	 * @return enemyCount as integer
	 */
	public int getEnemyCount() {
		return enemyCount;
	}
}