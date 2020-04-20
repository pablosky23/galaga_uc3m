package game;

import configuration.Constants;
import edu.uc3m.game.GameBoardGUI;
/*
 * 	###### CLASE SCREENELEMENTS ######
 * 	Esta clase contiene todos los elementos auxiliares
 * 	que se muestran por pantalla (textos, bonus, men�s, im�genes...)
 * 	con el fin de unificar todos estos elementos en un mismo objeto para facilitar
 * 	su acceso, uso y comprensi�n.
 * 	
 */

public class ScreenElements {
	// ## ATRIBUTOS ## \\
	// ~~ Tablero de la libreria ~~ \\
	private GameBoardGUI board;
	
	// ~~ Contadores ~~ \\
	private int counter[] = new int[5]; // Contador interno
	private int animPhase[] = new int[25]; // Fases de animaciones de los textos / imagenes
	
	// ## CONSTRUCTOR ## \\
	public ScreenElements(GameBoardGUI board) {
		this.board = board;
	}

	// #### M�TODOS #### \\
	// ~~ M�TODO DE ADICI�N DE TODOS LOS SPRITES DE IM�GENES AL TABLERO ~~ \\
	public void addAllImageSprites() {
		/*
		 * Nota: el orden de adici�n de los sprites dictamina su orden de capa, es
		 * decir, los sprites a�adidos en el tablero antes que otros se encuentran en
		 * capas inferiores por lo que los sprites que se han a�adido despu�s se podr�n
		 * superponer.
		 * 
		 * Es por esto que nos interesa a�adir el contador de puntuaci�n junto a otros
		 * elementos en primera instancia ya que si no lo hici�semos, en las pantallas
		 * de menus, pausa etc... el contador se ver�a por encima.
		 *
		 * ID's RESERVADAS [600 Thru 620] 
		 * 600 - Textos generales 
		 * 601 - Im�genes Bonus Peque�os 
		 * 602 - Men�s [Inicial, pausa]
		 * 603 - Logo del juego con animaci�n 
		 * 604 - Pantalla final [Victoria] 
		 * 605 - Fuego artificial 1 [Victoria] 
		 * 606 - Fuego artificial 2 [Victoria] 
		 * 607 - Fuego artificial 3 [Victoria]
		 * 608 - Texto de da�o recibido
		 * 610 Thru 614 - CONTADOR -
	     *		610 - D�gito Unidades 
		 *		611 - D�gito Decenas
		 *		612 - D�gito Centenas
		 *      613 - D�gito Millares 
		 *      614 - D�gito MilMillares
		 * 615 - Degradado de los bordes
		 * 640 - Planeta 1
		 * 641 - Planeta 2
		 * 
		 */

		// ~~ SPRITE DE MARCO CON DEGRADADO ~~ \\
		// Este sprite aporta un efecto de "desvanecimiento" a los enemigos
		// cuando salen por los bordes
		board.gb_addSprite(615, "img_gradientBorders.png", true);
		board.gb_setSpriteImage(615, "img_gradientBorders.png", 505, 652);
		board.gb_moveSpriteCoord(615, 86, 112);
		board.gb_setSpriteVisible(615, true);

		
		// ## SPRITES DE FUEGOS ARTIFICIALES [VICTORIA] ## \\
		for (int i = 0; i < 3; i++) {
			board.gb_addSprite(605 + i, "end_firework" + i + "0.png", true);
			board.gb_setSpriteImage(605 + i, "end_firework" + i + "0.png", 300, 300);
			board.gb_moveSpriteCoord(605 + i, -10 * (-190 * i), -20);

		}

		// ## SPRITE DE PANTALLA FINAL [VICTORIA y DERROTA] ## \\
		board.gb_addSprite(604, "img_victory0.png", true);
		board.gb_setSpriteImage(604, "img_victory0.png", 487, 640); // Debe estar en una de las primeras capas
																	// para que el contador
																	// aparezca por encima de esta imagen
		board.gb_moveSpriteCoord(604, 84, 110);
		// ## SPRITES DEL CONTADOR DE PUNTUACI�N ## \\
		// ~~ Sprite de Unidades ~~ \\
		board.gb_addSprite(610, "scoreNumber_0.png", true);
		board.gb_setSpriteImage(610, "scoreNumber_0.png", 40, 40);
		board.gb_moveSpriteCoord(610, 105, 210);
		// ~~ Sprite de Decenes ~~ \\
		board.gb_addSprite(611, "scoreNumber_0.png", true);
		board.gb_setSpriteImage(611, "scoreNumber_0.png", 40, 40);
		board.gb_moveSpriteCoord(611, 95, 210);
		// ~~ Sprite de Centenas ~~ \\
		board.gb_addSprite(612, "scoreNumber_0.png", true);
		board.gb_setSpriteImage(612, "scoreNumber_0.png", 40, 40);
		board.gb_moveSpriteCoord(612, 85, 210);
		// ~~ Sprite de Millares ~~ \\
		board.gb_addSprite(613, "scoreNumber_0.png", true);
		board.gb_setSpriteImage(613, "scoreNumber_0.png", 40, 40);
		board.gb_moveSpriteCoord(613, 75, 210);
		// ~~ Sprite de MilMillares ~~ \\
		board.gb_addSprite(614, "scoreNumber_0.png", true);
		board.gb_setSpriteImage(614, "scoreNumber_0.png", 40, 40);
		board.gb_moveSpriteCoord(614, 65, 210);
		// ~~ Hacer los d�gitos visibles ~~ \\
		for (int i = 610; i < 615; i++) {
			board.gb_setSpriteVisible(i, true);
		}

		// ~~ SPRITE DE TEXTO / AVISOS ~~ \\
		board.gb_addSprite(600, "txt_Welcome.png", true);
		board.gb_setSpriteImage(600, "txt_Welcome.png", 400, 201);
		board.gb_moveSpriteCoord(600, 84, 95);

		board.gb_addSprite(608, "txt_Damaged.png", true);
		board.gb_setSpriteImage(608, "txt_Damaged.png", 400, 201);
		board.gb_moveSpriteCoord(608, 84, 95);

		// ~~ SPRITES DE MEN�S [Inicial, pausa] ~~ \\
		board.gb_addSprite(602, "img_menuSound.png", true);
		board.gb_setSpriteImage(602, "img_menuSound.png", 487, 640);
		board.gb_moveSpriteCoord(602, 84, 110);

		// ## SPRITE DE LOGO ANIMADO ## \\
		board.gb_addSprite(603, "img_logo0.png", true);
		board.gb_setSpriteImage(603, "img_logo0.png", 400, 201);
		board.gb_moveSpriteCoord(603, 85, 48);

	}

	// ## M�TODOS ESPEC�FICOS ## \\
	// ~~ Mostrar los textos/im�genes (<nombre del texto>, mostrar) ~~ \\
	public void showText(String textName, boolean show) {
		if (show) {
			board.gb_setSpriteVisible(608, false);
			board.gb_setSpriteVisible(600, true);
			board.gb_setSpriteImage(600, "txt_" + textName + ".png", 400, 201);
		} else {
			board.gb_setSpriteVisible(600, false);
		}
	}
	// ~~ Esconder los textos (M�todo genera) ~~ \\
		public void hideText() {
			board.gb_setSpriteVisible(600, false);
			board.gb_setSpriteVisible(608, false);

		}
		
	// ~~ Mostrar el texto de da�o recibido ~~ \\
	public void showTextDamage(boolean show) {
		if (show) {
			hideText();
			board.gb_setSpriteVisible(608, true);
			board.gb_setSpriteImage(608, "txt_Damaged.png", 400, 201);
		} else {
			board.gb_setSpriteVisible(608, false);
		}
	}

	// ~~ M�todo para mostrar las im�genes inferiores de bonus (<nombre del bonus>,
	// mostrar) ~~ \\
	public void showBonusImage(String imageName, boolean show) {
		if (show) {
			board.gb_setSpriteImage(601, "img_" + imageName + ".png", 60, 60);
		} else {
			board.gb_setSpriteImage(601, "img_bonusEmpty.png", 60, 60);
		}
	}

	// ~~ Mostrar las im�genes del men� principal (<Menu>, mostrar) ~~ \\
	public void showMenu(String menuName, boolean show) {
		if (show) {
			board.gb_setSpriteVisible(602, true);
			board.gb_setSpriteImage(602, "img_" + menuName + ".png", 487, 640);
		} else {
			board.gb_setSpriteVisible(602, false);
		}
	}

	// ~~ Animar el logo inicial ~~ \\
	public void animateInitialLogo(int logoAnimPhase, boolean animate) {
		if (animate) {
			board.gb_setSpriteImage(603, "img_logo" + logoAnimPhase + ".png", 400, 201);
			board.gb_setSpriteVisible(603, true);
		} else {
			board.gb_setSpriteVisible(603, false);
		}

	}

	// ~~ Animar la pantalla de pausa ~~ \\
	public void animatePauseScreen(int pauseAnimPhase, boolean animate) {
		if (animate) {
			board.gb_setSpriteVisible(602, true);
			board.gb_setSpriteImage(602, "img_gamePaused" + pauseAnimPhase + ".png", 487, 640);
		} else {
			board.gb_setSpriteVisible(602, false);

		}
	}

	// ## M�TODOS DEL CONTADOR DE PUNTUACI�N ## \\
	// ~~ M�todo para mostrar los n�meros en el contador (n�mero, <Posici�n>,
	// mostrar ~~ \\
	public void showScoreNumber(int number, String position) {
		switch (position) {
		case "Units":
			board.gb_setSpriteImage(610, "scoreNumber_" + number + ".png", 40, 40);
			break;
		case "Tens":
			board.gb_setSpriteImage(611, "scoreNumber_" + number + ".png", 40, 40);
			break;
		case "Hundreds":
			board.gb_setSpriteImage(612, "scoreNumber_" + number + ".png", 40, 40);
			break;
		case "Thousands":
			board.gb_setSpriteImage(613, "scoreNumber_" + number + ".png", 40, 40);
			break;
		case "TenThousands":
			board.gb_setSpriteImage(614, "scoreNumber_" + number + ".png", 40, 40);
			break;
		}

	}

	// ~~ M�todo para obtener los d�gitos del contador (puntuaci�n) ~~ \\
	public void getDigits(int playerScore) {
		if (playerScore <= 99999) { // El l�mite es 99999, si se supera, el contador no se mostrar� m�s
			// El m�todo showScoreNumber(n�mero, Posici�n en el n�mero, show) establece las
			// im�genes a los sprites (divididos por d�gitos) dependiendo del n�mero que
			// est�
			// en esa posici�n teniendo en cuenta la puntuaci�n del jugador, obteniendo las
			// cifras mediante el m�dulo
			showScoreNumber(playerScore % 10, "Units");
			showScoreNumber(playerScore % 100 / 10, "Tens");
			showScoreNumber(playerScore % 1000 / 100, "Hundreds");
			showScoreNumber(playerScore % 10000 / 1000, "Thousands");
			showScoreNumber(playerScore % 100000 / 10000, "TenThousands");

		}
	}

	// ~~ M�todo para obtener los d�gitos del contador con animaci�n ~~ \\
	public boolean getDigitsAnimation(int playerScore) {
		if (playerScore <= 99999) { // El l�mite es 99999, si se supera, el contador no se mostrar� m�s
			// El m�todo showScoreNumber(n�mero, Posici�n en el n�mero, show) hace visibles
			// los sprites del contador teniendo
			// en cuenta la puntuaci�n del jugador, obteniendo las cifras mediante el m�dulo
			switch (animPhase[0]) {
			case 0:
				if (counter[0] != playerScore % 10) {
					showScoreNumber(counter[0], "Units");
					counter[0]++;
				} else {
					showScoreNumber(playerScore % 10, "Units");
					counter[0] = 0;
					animPhase[0] = 1;
				}
				break;
			case 1:
				if (counter[0] != playerScore % 100 / 10) {
					showScoreNumber(counter[0], "Tens");
					counter[0]++;
				} else {
					showScoreNumber(playerScore % 100 / 10, "Tens");
					counter[0] = 0;
					animPhase[0] = 2;
				}
				break;
			case 2:
				if (counter[0] != playerScore % 1000 / 100) {
					showScoreNumber(counter[0], "Hundreds");
					counter[0]++;
				} else {
					showScoreNumber(playerScore % 1000 / 100, "Hundreds");
					counter[0] = 0;
					animPhase[0] = 3;
				}
				break;
			case 3:
				if (counter[0] != playerScore % 10000 / 1000) {
					showScoreNumber(counter[0], "Thousands");
					counter[0]++;
				} else {
					showScoreNumber(playerScore % 10000 / 1000, "Thousands");
					counter[0] = 0;
					animPhase[0] = 4;
				}
				break;
			case 4:
				if (counter[0] != playerScore % 100000 / 10000) {
					showScoreNumber(counter[0], "TenThousands");
					counter[0]++;
				} else {
					showScoreNumber(playerScore % 100000 / 10000, "TenThousands");
					counter[0] = 0;
					animPhase[0] = 5;
				}
				break;
			case 5:
				return true;
			}
		}
		return false;
	}

	// ~~ M�todo para mover el Scoreboard a la posici�n final en la pantalla de
	// victoria ~~ \\
	public void moveScoreBoardEnd() {
		for (int i = 0; i < 5; i++) {
			board.gb_moveSpriteCoord(614 - i, 100 + (i * 10), 105);
		}
	}
	
	// ~~ M�todo para a�adir las vidas finales [pantalla de victoria] ~~ \\
	public void addEndLives() {
		for (int i = 0; i < Constants.MAXHEALTH; i++) {
			board.gb_addSprite(20 + i, "heartOut.png", true);
			board.gb_setSpriteImage(20 + i, "heartOut.png", 40, 40);
			board.gb_moveSpriteCoord(20 + i, 108 + i * 12, 120);
			board.gb_setSpriteVisible(20 + i, true);
		}
	}

	// ~~ Animar las vidas finales [pantalla de victoria] ~~ \\
	public void getLivesAnimation(int playersHealth) {
		if (animPhase[1] < playersHealth) {
			new sound.SoundLib("sound/sound_heartUp.wav").start();
			switch (animPhase[1]) {
			case 0:
				board.gb_setSpriteImage(20, "heart.png", 40, 40);
				animPhase[1]++;
				break;
			case 1:
				board.gb_setSpriteImage(21, "heart.png", 40, 40);
				animPhase[1]++;
				break;
			case 2:
				board.gb_setSpriteImage(22, "heart.png", 40, 40);
				animPhase[1]++;
				break;
			}
		}

	}

	// ## FUEGOS ARTIFICIALES FINALES ## \\
	public void getFireworkEndEffect(int fireworkNumber) {
		board.gb_moveSpriteCoord(605 + fireworkNumber, animPhase[12 + fireworkNumber],
				animPhase[6 + fireworkNumber]);
		board.gb_setSpriteVisible(605 + fireworkNumber, true);
		board.gb_setSpriteImage(605 + fireworkNumber,
				"end_firework" + fireworkNumber + animPhase[15 + fireworkNumber] + ".png", 300, 300);
		if (animPhase[15 + fireworkNumber] < 7) {
			animPhase[15 + fireworkNumber]++;
		} else {
			animPhase[15 + fireworkNumber] = 0;
			animPhase[12 + fireworkNumber] = (int) (Math.random() * (140 - (30 + 10 * fireworkNumber)) + 1) + 30;
			animPhase[6 + fireworkNumber] = (int) (Math.random() * (190 - (40 + 10 * fireworkNumber)) + 1) + 40;
		}

	}
	public void setCounter(int[] counter) {
		this.counter = counter;
	}
	
	public int[] getCounter() {
		return counter;
	}



}
