package game;

import java.awt.Color;
import java.util.Locale;

import configuration.Constants;
import edu.uc3m.game.GameBoardGUI;

/**
 * Recreaci�n del videojuego "G�laga" realizada por Pablo D�az-Heredero Garc�a
 * | Grupo 81 [Universidad Carlos III de Madrid] |
 * ---------------------------------------------------------------------------
 * La clase "Main" contiene toda la ejecuci�n del juego, incluyendo las
 * pantallas de men� inicial, el loop del juego principal, el men� de pausa y la
 * pantalla final.
 * 
 * Adjuntada al proyecto se encuentra una memoria en formato PDF en la cual se
 * detallan los diferentes m�todos y las estrategias utilizadas para obtener el
 * resultado funcional final de este juego. No obstante, existen diversos
 * comentarios en el c�digo de este proyecto los cuales aportan informaci�n
 * espec�fica y r�pida sobre la funci�n de determinadas secciones del mismo.
 * 
 * Los comentarios se encuentran redactados en espa�ol, sin embargo, el juego y
 * las variables utilizadas se han elaborado en ingl�s (por comodidad y por
 * facilitar la interpretaci�n del c�digo).
 * ---------------------------------------------------------------------------
 * 
 * @author Pablo D�az-Heredero Garc�a | Grupo 81
 * @since Nov 2018
 * @version 0.9C
 *
 */

public class Main {
	public static void main(String[] args) throws InterruptedException {
		// ## ELEMENTOS INICIALES ## \\
		// ~~ Idioma ~~ \\
		Locale.setDefault(new Locale(Constants.LANGUAGE));

		// ~~ CREACI�N DEL TABLERO ~~ \\
		GameBoardGUI board = new GameBoardGUI(Constants.WIDTH, Constants.HEIGHT);

		// ## OBJETOS PRINCIPALES DEL JUEGO ## \\
		// ~~ Generaci�n del Objeto Jugador ~~ \\
		Player pl = new Player(1, 85, 190, Constants.DIR_N, Constants.MAXHEALTH, 0, "player.png");
		// ~~ Creaci�n de la matriz de objetos enemigos ~~ \\
		Enemy enemies[][] = new Enemy[Constants.LV1FORMATION.length][Constants.LV1FORMATION[0].length]; // La primera
																										// matriz en
																										// Constants
																										// define el
																										// n�mero de
																										// columnas y el
																										// n�mero de
																										// filas de la
																										// formaci�n
		// ~~ Generaci�n del Objeto de Elementos en Pantalla ~~ \\
		ScreenElements screenElement = new ScreenElements(board); // Objeto screenElement [En el que se almacenan los
																	// m�todos referentes a textos e im�genes del
																	// juego]
		// ## VARIABLES DEL JUEGO ## \\
		// ~~ Variables ~~ \\
		int var_actualLevel = 0; // Almacena el n�mero de nivel actual
		int var_bonusCounter = 0; // Almacena el n�mero de bonus actual
		int var_shotCounter = 0; // Contador de disparos que se utiliza para recorrer el array de disparos del
									// jugador
		int var_zakoCounter = 0; // Contador de Zakos
		int var_goeiCounter = 0; // Contador de Goeis
		int var_commanderCounter = 0; // Contador de Comandantes G�laga
		int var_enterpriseCounter = 0; // Contador de Enterprises
		int var_killedEnemies = 0; // Contador de enemigos eliminados
		String var_playerName[] = new String[3]; // Este array almacena en la tercera posici�n el nombre del jugador
													// para posteriormente poder ser introducido mediante el bot�n
													// "Nuevo"

		var_playerName[2] = Constants.PLAYERNAME; // Establecemos el nombre inicial del jugador como el especificado en
													// la configuraci�n

		// ~~ Array de contadores de uso espec�fico en el juego ~~ //
		/*
		 * Con el fin de utilizar m�todos explicados en clase, se ha hecho uso de
		 * contadores que incrementan y reinician su valor dependiendo de la situaci�n a
		 * la cual est�n destinados en lugar de utilizar el tiempo del sistema como
		 * m�todo de control temporal en el juego.
		 * 
		 * Estos contadores tienen una funci�n concreta y est�n individualizados de
		 * forma que cada uno tiene solamente su funci�n (salvo en alg�n caso especial).
		 * De esta manera de ning�n modo podr�n interferir entre ellos.
		 * 
		 * [Funci�n de cada contador especificada en la MEMORIA]
		 * 
		 */
		int var_counter[] = new int[27];

		// Fases de la Animaci�n del Logo de inicio \\
		int logoAnimPhase = 0;

		// ## TRIGGERS DEL JUEGO: ACTIVACI�N DE SECCIONES DEL C�DIGO ## \\
		/*
		 * Uno de los elementos primordiales a la hora de trabajar con c�digos
		 * contenidos en un bucle constante es el trigger. Un trigger es un boolean que
		 * sirve como "gatillo" para accionar ciertas secciones del c�digo en
		 * determinados momentos. Se ha basado gran parte del funcionamiento del juego
		 * en estos triggers, los cuales aportan numerosas ventajas.
		 * 
		 */
		boolean trigger_levelStart = false; // Se activa cuando el nivel ha comenzado
		boolean trigger_enemiesReachedFormation = false; // Se activa cuando los enemigos han terminado su entrada
		boolean trigger_formationReachedMax = false; // Se activa si la formaci�n toca el borde derecho y se
														// desactiva cuando la formaci�n toca el izquierdo
		boolean trigger_formationAnimated = false; // Se activa cuando queremos que los enemigos se muevan de un
													// lado a otro y se animen sus sprites
		boolean trigger_gameRunning = false; // Se activa si el juego comienza
		boolean trigger_welcomeShowed = false; // Se activa si se ha mostrado el logo del juego de inicio
		boolean trigger_enemiesFreezed = false; // Se activa si se ha activado la congelaci�n de enemigos [mediante
												// bonus o comando]
		boolean trigger_enemyDefeatedTxtShowing = false; // Se activa si se ha eliminado a un comandante galaga o a un
															// enterprise para
															// mostrar el texto de su eliminaci�n y controlar la
															// desaparici�n del mismo
		boolean trigger_textShowing = false; // Se activa si determinados textos est�n mostr�ndose para controlar el
												// tiempo
												// de desaparici�n
		boolean trigger_bonusActive = false; // Se activa si se ha recibido un bonus hasta que se haya usado
		boolean trigger_bonusInvincibleActive = false; // Se activa si se ha utiliazdo el bonus de invencibilidad
		boolean trigger_spacePressed = false; // Controla cuando se ha pulsado el espacio y a�ade un disparo en ese
												// caso. Vuelve a establecerse en false cuando el disparo se ha
												// a�adido
		boolean trigger_soundsActive = true; // Permite la activaci�n / desactivaci�n de los sonidos del juego
		boolean trigger_godModeActive = false; // Se activa si se ha introducido el comando "god"
		boolean trigger_gamePaused = false; // Se activa si el juego se ha pausado
		boolean trigger_endFireworksActive = false; // Se activa si se ha logrado una victoria y se han alcanzado los
													// puntos m�nimos para obtener una medalla. Activa la aparici�n
													// aleatoria de fuegos artificiales
		boolean trigger_shotReceived = false; // Se activa si el jugador recibe un disparo
		boolean trigger_allEnemyDead = false; // Se activa si se ha matado a todos los enemigos mediante el comado
												// killEnemies
		boolean trigger_levelGenerated = false; // Se activa cuando el nivel ha sido generado por completo [Adici�n de
												// enemigos]
		boolean trigger_levelCompleted = false; // Se activa cuando el nivel ha sido completado

		boolean trigger_levelCommandIntroduced = false; // Se activa cuando se ha introducido el comando para cambiar a
														// otro nivel

		// ## ESTABLECIMIENTO DE LOS ATRIBUTOS INICIALES DEL TABLERO ## \\
		board.setTitle(Constants.BOARDTITLE); // T�tulo de la ventana
		board.gb_setPortraitPlayer(Constants.PORTRAIT); // Retrato del jugador
		board.gb_println(Constants.WELCOMEPHRASE); // Frase inicial al ejecutar el juego
		board.gb_setTextAbility1("Shots");
		board.gb_setTextAbility2("Hits");
		board.gb_setTextLevel("Level");
		board.gb_setValueLevel(var_actualLevel);
		board.gb_setTextPointsDown("Speed");
		board.gb_setTextPointsUp("Points");
		board.gb_setGridColor(0, 0, 0); // Establece la cuadr�cula en negro para hacerla lo menos visible posible
		board.gb_setTextPlayerName(var_playerName[2]);

		// ## GENERACI�N DEL FONDO DEL NIVEL ## \\

		// ~~ Colorear el fondo en negro ~~ \\
		for (int ii = 0; ii < Constants.WIDTH; ii++) { // Recorre las filas
			for (int jj = 0; jj < Constants.HEIGHT; jj++) { // Recorre las columnas
				board.gb_setSquareColor(ii, jj, 0, 0, 0);
			}
		}
		/*
		 * El fondo es coloreado de negro. De esta forma, si una imagen de la
		 * composici�n aleatoria fallase, el fondo no ser�a un cuadrado blanco
		 */

		// ~~ Generaci�n del fondo de estrellas aleatorio ~~ \\
		for (int ii = 0; ii < Constants.WIDTH; ii++) { // Recorre las filas
			for (int jj = 0; jj < Constants.HEIGHT; jj++) { // Recorre las columnas
				int numberBg = (int) (Math.random() * 9);
				board.gb_setSquareImage(ii, jj, "bg10" + numberBg + ".png");
				/*
				 * La composici�n del fondo es generada de forma aleatoria a partir de im�genes
				 * preestablecidas en el juego
				 */
			}
		}

		// ~~ Imagen superpuesta de los planetas ~~ \\
		/*
		 * Con el fin de ambientar el juego, existe una imagen superpuesta que contiene
		 * planetas. Se trata de 3 im�genes las cuales son seleccioandas por el juego de
		 * forma aleatoria
		 */
		board.gb_addSprite(640, "bg_planets.png", true);
		board.gb_setSpriteImage(640, "bg_planets" + (int) (Math.random() * 3) + ".png", 487, 640);
		/*
		 * La selecci�n aleatoria determina el n�mero del archivo:
		 * "bg_planters[n�m. aleatorio 0 a 2].png";
		 */
		board.gb_moveSpriteCoord(640, 84, 110);
		board.gb_setSpriteVisible(640, true);

		// ## CONFIGURACIONES PREVIAS AL INICIO DEL JUEGO ## \\

		/*
		 * Los elementos del juego est�n designados mediante un sistema de ID's. Con el
		 * fin de permitir la mayor flexibilidad posible a la hora de a�adir elementos y
		 * de identificarlos de forma intuitiva, se han definido las ID's por
		 * centenares.
		 * 
		 * ///// ID's RESERVADAS /////
		 * // Player: 1 
		 * // Player Lives: 2 + (MAXHEALTH - 1) | (ID's 2 a 4 si MAXHEALTH = 3)
		 * // Enemy Zako: 1XX 
		 * // Enemy Goei: 2XX 
		 * // Enemy Commander: 3XX 
		 * // Enemy Enterprise: 4XX 
		 * // Textos e im�genes adicionales: 6XX 
		 * // Player Torpedos: 7XX 
		 * // Enemy Torpedos: >2XXX 
		 * // [Determinadas por el N�mero de ID del enemigo multiplicado por 10 + sus disparos]
		 * ///////////////////////////
		 * 
		 * Las ID's m�s espaciadas entre s�, son los torpedos. A pesar de que los arrays
		 * de torpedos son vaciados cuando estos salen del mapa o impactan contra uno de
		 * los objetos designados (enemigo, jugador) y su ID es reutilizada (solo se
		 * utilizan ID's hasta el n�mero m�ximo de torpedos designado en Constants), se
		 * ha decidido dejar un margen muy amplio, de forma que el n�mero de torpedos
		 * m�ximo pueda ser una cantidad muy grande.
		 * Adem�s, los enemigos utilizan unas ID's muy grandes para los torpedos, ya que
		 * cada torpedo utiliza como identificador la ID del enemigo multiplicada por 10
		 * m�s el n�mero de disparo de ese enemigo. De esta manera se evitan posibles
		 * superposiciones entre las ID's de los disparos de los enemigos.
		 * 
		 */

		// ~~ Hacer visible el tablero ~~ \\
		board.getContentPane().setBackground(Color.BLACK); // Establecer la ventana del juego en color negro
		board.setVisible(true); // Hacer visible el tablero
		new sound.SoundLib("sound/sound_initialization.wav").start(); // Sonido al abrir el juego

		// ~~ Adici�n del jugador al tablero ~~ \\
		board.gb_addSprite(pl.getID(), pl.getSprite(), true);
		// ~~ Aspecto inicial del Sprite del jugador ~~ \\
		board.gb_setSpriteImageDouble(pl.getID(), pl.getSprite());
		// ~~ Posici�n inicial del jugador ~~ \\
		board.gb_moveSpriteCoord(pl.getID(), pl.getX(), pl.getY());
		// ~~ Hacer visibles el Sprite del jugador ~~ \\
		board.gb_setSpriteVisible(pl.getID(), true);

		// ~~ Generaci�n de los Corazones en pantalla ~~ \\
		for (int i = 0; i < Constants.MAXHEALTH; i++) { // Se a�aden tantos corazones como vidas 
														// se hayan designado en Constants
			board.gb_addSprite(2 + i, "heart.png", true);
			board.gb_setSpriteImage(2 + i, "heart.png", 40, 40);
			board.gb_moveSpriteCoord(2 + i, 11 + i * 11, 210);
			board.gb_setSpriteVisible(2 + i, true);
		}

		// ~~ A�adir todos los elementos de pantalla al tablero ## \\
		screenElement.addAllImageSprites(); // Con este m�todo se a�aden todas las im�genes al juego.
		
		// ~~ SPRITE DE BONUS PEQUE�O ## \\
		board.gb_addSprite(601, "img_bonusEmpty.png", true); // Por defecto se a�ade el bonus vac�o.
		board.gb_setSpriteImage(601, "img_bonusEmpty.png", 60, 60);
		board.gb_moveSpriteCoord(601, 157, 207);
		board.gb_setSpriteVisible(601, true); // Visible por defecto
		// ## Mostrar el menu y el logo inicial ## \\
		screenElement.showMenu("menuSound", true);
		screenElement.animateInitialLogo(0, true);

		// ## LOOP MEN� INICIAL ## \\
		// ####################### \\
		/*
		 * Con el fin de maximizar las posibilidades del juego, se ha construido un men�
		 * inicial, basado en im�genes (cada men� es una imagen en si), las cuales son
		 * llamadas dependiendo de la tecla que se haya pulsado o las opciones que se
		 * hayan seleccionado previamente.
		 * 
		 */
		do {

			// ~~ Establecer valores del tablero iniciales antes de comenzar el juego ~~ \\
			board.gb_setValueAbility1(0);
			board.gb_setValueAbility2(0);
			board.gb_setValueHealthMax(Constants.MAXHEALTH);
			board.gb_setValueHealthCurrent(pl.getHealth());
			board.gb_setValuePointsDown(pl.getSpeed());

			// ~~ Animaci�n del logo inicial ~~ \\
			if (var_counter[0] < 3) {
				var_counter[0]++;
			} else {
				if (logoAnimPhase < 8) {
					logoAnimPhase++;

				} else {
					logoAnimPhase = 0;
				}
				screenElement.animateInitialLogo(logoAnimPhase, true);
				var_counter[0] = 0;
			}

			// ~~ Obtenci�n del firstAction, entrada de teclado inicial ~~ \\
			String firstAction = board.gb_getLastAction().trim();
			if (firstAction.contains("new game")) {
				var_playerName = firstAction.split(" "); // Obtenci�n del nombre del jugador
				board.gb_showMessageDialog("Player's name set to " + var_playerName[2]);
			}
			if (firstAction.equals("exit game")) {
				board.gb_showMessageDialog(
						"\t - GAME FINISHED - \n \n \t Pablo D�az-Heredero Garc�a \n \n \t \n [uc3m - 2018]");
				System.exit(0);
			}
			board.gb_setTextPlayerName(var_playerName[2]);
			if (firstAction.contains("command")) { // Si se introduce un comando se muestra el mensaje
				board.gb_println("\n Commands are only available when the game has started!");
			}
			switch (firstAction) {
			case "up": // Comando que permite ocultar la ventana
				switch (var_counter[1]) {
				case 0:
					board.setSize(519, 687);
					var_counter[1]++;
					new sound.SoundLib("sound/sound_showWindow.wav").start();
					break;
				case 1:
					board.setSize(824, 709);
					var_counter[1] = 0;
					new sound.SoundLib("sound/sound_hideWindow.wav").start();
					break;
				}
				break;
			case "right": // Men� selector de velocidad
				if (var_counter[2] == 0) {
					switch (var_counter[3]) {
					case 0:
						switch (pl.getSpeed()) { // Muestra la ventana de velocidad dependiendo de la velocidad actual
						case 3:
							screenElement.showMenu("menuSpeed1", true);
							break;
						case 6:
							screenElement.showMenu("menuSpeed2", true);
							break;
						case 9:
							screenElement.showMenu("menuSpeed3", true);
							break;
						}
						new sound.SoundLib("sound/sound_help.wav").start();
						var_counter[3]++;
						break;
					case 1:
						if (trigger_soundsActive) { // Vuelve al men� inicial, teniendo en cuenta si est� activo o
													// desactivado el sonido
							screenElement.showMenu("menuSound", true);
						} else {
							screenElement.showMenu("menuNoSound", true);
						}
						new sound.SoundLib("sound/sound_hideHelp.wav").start();
						var_counter[3] = 0;
						break;
					}
				} else {
					new sound.SoundLib("sound/sound_notInMenu.wav").start(); // Si el jugador no se encuentra en la
					// pantalla de men�, al no funcionar
					// otras teclas distintas de "up" se
					// reproduce este sonido
				}
				break;
			case "tab": // Opci�n de activar / desactivar el sonido
				if (var_counter[2] == 0 && var_counter[3] == 0) {
					// Solo funcionar� si
					// counter[2] == 0 y counter[3] == 0, es
					// decir, si no est� mostr�ndose otra ventana distinta del men� inicial
					switch (var_counter[4]) {
					case 0:
						screenElement.showMenu("menuNoSound", true);
						trigger_soundsActive = false;
						new sound.SoundLib("sound/sound_unactive.wav").start();
						var_counter[4]++;
						break;
					case 1:
						screenElement.showMenu("menuSound", true);
						trigger_soundsActive = true;
						new sound.SoundLib("sound/sound_active.wav").start();
						var_counter[4] = 0;
						break;
					}
				} else {
					new sound.SoundLib("sound/sound_notInMenu.wav").start(); // Si el jugador no se encuentra en la
					// pantalla de men�, al no funcionar
					// otras teclas distintas de "up" se
					// reproduce este sonido
				}
				break;
			case "down": // Muestra la ventana de ayuda
				if (var_counter[3] == 0) {
					switch (var_counter[2]) {
					case 0:
						screenElement.showMenu("menuHelp", true);
						new sound.SoundLib("sound/sound_help.wav").start();
						var_counter[2]++;
						break;
					case 1: // Vuelve al men� inicial teniendo en cuenta si el sonido est� activado o
							// desactivado
						screenElement.showMenu("menuHelp", false);
						if (trigger_soundsActive) {
							screenElement.showMenu("menuSound", true);
						} else {
							screenElement.showMenu("menuNoSound", true);
						}
						new sound.SoundLib("sound/sound_hideHelp.wav").start();
						var_counter[2] = 0;
						break;
					}
				}
				break;
			case "space": 
				if (var_counter[3] == 1) { // Si el contador var_counter[3] == 1, el jugador se encuentra
					// en la ventana de velocidad. Permite seleccionar las distintas velocidades pulsando espacio
					switch (pl.getSpeed()) {
					case 3:
						pl.setSpeed(6);
						screenElement.showMenu("menuSpeed2", true);
						new sound.SoundLib("sound/sound_speed.wav").start();

						break;
					case 6:
						pl.setSpeed(9);
						screenElement.showMenu("menuSpeed3", true);
						new sound.SoundLib("sound/sound_speed.wav").start();

						break;
					case 9:
						pl.setSpeed(3);
						screenElement.showMenu("menuSpeed1", true);
						new sound.SoundLib("sound/sound_speed.wav").start();

						break;

					}
				} else if (var_counter[3] == 0 && var_counter[2] == 0) { // Si el jugador no se encuentra en ninguna
																			// ventana que no sea la del men� inicial,
																			// el espacio permite iniciar el juego
					new sound.SoundLib("sound/sound_start.wav").start();
					screenElement.showMenu("MenuSound", false);
					screenElement.showMenu("MenuNoSound", false);
					screenElement.animateInitialLogo(logoAnimPhase, false);
					trigger_gameRunning = true; // Activa el loop del juego [trigger_gameRunning] y desactiva el del
												// men�
				} else {
					new sound.SoundLib("sound/sound_notInMenu.wav").start(); // Si el jugador no se encuentra en la
																				// pantalla de men�, al no funcionar
																				// otras teclas distintas de "up" se
				}

				break;

			}

			Thread.sleep(30L);
		} while (!trigger_gameRunning); // Se ejecuta mientras el juego no haya empezado

		// ## LOOP PRINCIPAL DEL JUEGO ## \\
		// ############################## \\
		do {
			// ## CAPTURA DE LASTACTION ## \\
			// Captura en lastAction la �ltima tecla pulsada y trim() elimina los espacios
			String lastAction = board.gb_getLastAction().trim();

			// ~~ Si se pulsa el bot�n "Nuevo" ~~ \\
			if (lastAction.contains("new game")) {
				var_playerName = lastAction.split(" ");
				board.gb_setTextPlayerName(var_playerName[2]);
				board.gb_showMessageDialog("Player's name set to " + var_playerName[2]);
				board.gb_showMessageDialog("A new game has started");
				trigger_levelGenerated = false;
				// ~~ Eliminaci�n de los disparos enemigos y de los enemigos ~~ \\
				for (int i = 0; i < enemies.length; i++) {
					for (int j = 0; j < enemies[i].length; j++) {
						if (enemies[i][j] != null) {
							for (int s = 0; s < enemies[i][j].shot.length; s++) {

								if (enemies[i][j].shot[s] != null) {
									board.gb_setSpriteVisible(enemies[i][j].shot[s].getID(), false);
								}

							}
							board.gb_setSpriteVisible(enemies[i][j].getID(), false);
							enemies[i][j] = null;
						}
					}
				}
				var_actualLevel = 0;

			}

			// ~~ Switch Movimiento / Acciones del Jugador ~~ \\
			switch (lastAction) {
			case "tab":
				screenElement.animatePauseScreen(3, true);
				new sound.SoundLib("sound/sound_pause.wav").start();
				trigger_gamePaused = true;
				break;
			// Teclado: Flecha derecha
			case "right":
				if (pl.getHealth() != 0 && trigger_levelStart) {
					if (pl.getX() < 160) { // L�mite derecho del tablero | Right board limit
						pl.move(Constants.DIR_E, 1); // Se mueve 1 paso en direcci�n Este | Moves 1 step
														// on
														// East

					}
				}
				break;

			// Teclado: Flecha izquierda
			case "left":
				if (pl.getHealth() != 0 && trigger_levelStart) {

					if (pl.getX() > 15) { // L�mite izquierdo del tablero | Right board limit
						pl.move(Constants.DIR_W, 1); // Se mueve 1 paso en direcci�n Oeste | Moves 1
														// step on
														// West
														// direction
					}
				}
				break;

			// Teclado: Espacio
			case "space":
				if (var_counter[5] < 3) {
					var_counter[5]++;
				} else {
					var_counter[5] = 0;
					board.gb_clearCommandBar();
					// Vac�a la barra de comandos si se han introducido m�s de 3 espacios [permite
					// escribir comandos espaciados]
				}
				if (pl.getHealth() != 0 && trigger_levelStart) {

					if (var_shotCounter < pl.shot.length) {
						var_shotCounter++;
					} else {
						var_shotCounter = 0; // El contador de disparos se reinicia si supera el n�mero de elementos del
												// array (disparos m�ximos)
					}
					trigger_spacePressed = true;
					for (int i = 0; i < pl.shot.length; i++) {

						if (pl.shot[i] == null && trigger_spacePressed) {
							pl.shot[i] = new Torpedo(700 + i, pl.getX(), pl.getY(), "torpedo100.png");
							if (trigger_soundsActive) {
								new sound.SoundLib("sound/sound_shot.wav").start();
							}
							board.gb_addSprite(pl.shot[i].getID(), pl.shot[i].getSprite(), true);
							board.gb_moveSprite(pl.shot[i].getID(), pl.shot[i].getX(), pl.shot[i].getY());
							board.gb_setSpriteVisible(pl.shot[i].getID(), true);
							pl.setShotCount(pl.getShotCount() + 1);
							trigger_spacePressed = false;

						}

					}

				}

				break;

			// Teclado: Flecha arriba
			case "up":
				if (trigger_bonusActive && trigger_levelStart) {
					switch (var_bonusCounter) {
					case 1:
					case 3:
					case 5:
						board.gb_clearCommandBar();
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								if (enemies[i][j] != null) {
									enemies[i][j].setFreezed(true);
									trigger_enemiesFreezed = true;
									board.gb_setSpriteImage(enemies[i][j].getID(), enemies[i][j].getSprite());
								}
							}
						}
						screenElement.showText("Freezed", true);
						trigger_bonusActive = false;
						screenElement.showBonusImage("Bonus1Small", false);
						if (trigger_soundsActive) {
							new sound.SoundLib("sound/sound_freeze.wav").start();
						}

						break;
					case 2:
					case 4:
					case 6:
						trigger_bonusActive = false;
						screenElement.showBonusImage("Bonus2Small", false);
						if (trigger_soundsActive) {
							new sound.SoundLib("sound/sound_invencible.wav").start();
						}
						pl.setInvencible(true);
						trigger_bonusInvincibleActive = true;
						break;

					}

				} else {
					if (var_bonusCounter == 0) {
						board.gb_println("\n You haven't won any bonus yet");
					} else {
						board.gb_println("\n You have already used your previous bonus");
					}

				}

				break;

			// ~~ COMANDOS ~~ \\
			/*
			 * El juego contiene m�ltiples comandos los cuales ofrecen ayuda a la hora de
			 * completar el mismo o probar sus distintas funcionalidades.
			 *
			 * Con el fin de mantener una separaci�n l�gica entre los comandos, se han
			 * dividido en dos grupos [visibles al introducir el comando help en la
			 * consola]: comandos de usuario (�tiles para un usuario com�n) y comandos de
			 * desarrollador (�tiles para evaluar la pr�ctica y comprobar el funcionamiento
			 * de los distintos elementos)
			 *
			 */
			case "command freeze": // Comando de congelaci�n de los enemigos
				board.gb_clearCommandBar();
				for (int i = 0; i < enemies.length; i++) {
					for (int j = 0; j < enemies[i].length; j++) {
						if (enemies[i][j] != null) {
							enemies[i][j].setFreezed(true);
							trigger_enemiesFreezed = true;
							board.gb_setSpriteImage(enemies[i][j].getID(), enemies[i][j].getSprite());
						}
					}
				}
				screenElement.showText("Freezed", true);
				trigger_bonusActive = false;
				screenElement.showBonusImage("Bonus1Small", false);
				if (trigger_soundsActive) {
					new sound.SoundLib("sound/sound_freeze.wav").start();
				}
				break;
			case "command killPlayer": // Establece la vida del jugador a 0 y termina el juego en la pantalla de
										// DERROTA
				board.gb_clearCommandBar();
				pl.setHealth(0);
				break;
			case "command killEnemies": // Elimina a todos los enemigos del nivel y lo completa
				board.gb_clearCommandBar();
				if (trigger_soundsActive) {
					new sound.SoundLib("sound/sound_explosion.wav").start();
				}
				for (int i = 0; i < enemies.length; i++) {
					for (int j = 0; j < enemies[i].length; j++) {
						if (enemies[i][j] != null) {
							enemies[i][j].setSprite("explosion24.png");
							enemies[i][j].setDeadAnimation(true);
							enemies[i][j].setHealth(0);
							trigger_allEnemyDead = true;
						}
					}
				}
				trigger_levelCompleted = true;
				break;

			case "command stop":
				board.gb_clearCommandBar();
				board.gb_println("Game execution stopped for 30 seconds");
				Thread.sleep(30000);
				break;
			case "command pause":
				board.gb_clearCommandBar();
				trigger_gamePaused = true;
				break;

			case "command level 1":
				board.gb_clearCommandBar();
				if (var_actualLevel == 1) {
					board.gb_println("\n" + "You are already stablished in LEVEL 1");
				} else {
					var_actualLevel = 1;
					trigger_levelCommandIntroduced = true;
					trigger_levelCompleted = true;

				}
				break;
			case "command level 2":
				board.gb_clearCommandBar();
				if (var_actualLevel == 2) {
					board.gb_println("\n" + "You are already stablished in LEVEL 2");
				} else {
					var_actualLevel = 2;
					trigger_levelCommandIntroduced = true;
					trigger_levelCompleted = true;

				}
				break;
			case "command level 3":
				board.gb_clearCommandBar();
				if (var_actualLevel == 3) {
					board.gb_println("\n" + "You are already stablished in LEVEL 3");
				} else {
					var_actualLevel = 3;
					trigger_levelCommandIntroduced = true;
					trigger_levelCompleted = true;

				}
				break;
			case "command level 4":
				board.gb_clearCommandBar();
				if (var_actualLevel == 4) {
					board.gb_println("\n" + "You are already stablished in LEVEL 4 [FINAL LEVEL]");
				} else {
					var_actualLevel = 4;
					trigger_levelCommandIntroduced = true;
					trigger_levelCompleted = true;

				}
				break;
			case "command playerPoints++": // Aumenta 5000 puntos
				board.gb_clearCommandBar();
				pl.upScore(5000);
				break;
			case "command playerPointsUp": // Aumenta 1 punto
				board.gb_clearCommandBar();
				pl.upScore(1);
				break;
			case "command reset stats":
				board.gb_clearCommandBar();
				pl.resetAttributes(); // Reinicia todos los atributos del jugador
				break;
			case "command clearConsole":
				board.gb_clearCommandBar();
				board.gb_clearConsole();
				break;
			case "command hideWindow":
				board.gb_clearCommandBar();
				board.setSize(511, 680);
				break;
			case "command decreaseHealth":
				board.gb_clearCommandBar();
				pl.setHealth(pl.getHealth() - 1);
				break;
			case "command god":
				if (var_counter[26] == 0) {
					screenElement.showTextDamage(false);
					board.gb_clearCommandBar();
					if (trigger_soundsActive) {
						new sound.SoundLib("sound/sound_angel.wav").start();
					}
					board.gb_setPortraitPlayer("galagaLogoGod.png"); // Retrato del jugador
					trigger_godModeActive = true;
					pl.setSprite("playerGod2.png");
					pl.setInvencible(true);
					var_counter[6] = 25; // Establecer este contador con el valor de 25 hace que el jugador sea
											// invencible siempre y cuando est� el modo dios activado, deshabilitando la
											// invencibilidad temporal establecida por bonus o por da�o al jugador
					var_counter[26]++; // Este contador controla las veces de introducci�n del comando god
				} else {
					board.gb_clearCommandBar();
					pl.setSprite("player.png");
					board.gb_setPortraitPlayer(Constants.PORTRAIT); // Retrato del jugador
					trigger_godModeActive = false;
					pl.setInvencible(false);
					var_counter[6] = 0;
					var_counter[26] = 0;
				}
				break;

			case "command help":
				board.gb_showMessageDialog("" + "## COMMAND LIST ## " + "\n ~~ USER COMMANDS ~~ "
						+ "\n help: Shows Command List " + "\n god: Sets/unsets player invincible "
						+ "\n level <level number>: Goes to level <level number>"
						+ "\n endGame: Finishes actual game as a VICTORY" + "\n freeze: Sets enemies freezed"
						+ "\n clearConsole: Clears console messages" + "\n hideWindow: Hides game's Window"
						+ "\n pause: Sets/unsets game's state to pause | Same as [TAB]"
						+ "\n \n ~~ DEVELOPER COMMANDS ~~" + "\n stop: Pauses game's execution for 30 seconds"
						+ "\n playerPoints++: Adds 5000 points to player's scoreboard"
						+ "\n playerPointsUp: Adds 1 point to player's scoreboard"
						+ "\n decreaseHealth: Decreases player's health by one life"
						+ "\n reset stats: resets player's stats"
						+ "\n killPlayer: Sets player's health to 0 and ends game as DEFEAT"
						+ "\n killEnemies: Kills all enemies and completes actual level");
				break;
			case "command endGame":
				trigger_gameRunning = false;
				break;

			case "exit game":
				board.gb_showMessageDialog(
						"\t - GAME FINISHED - \n \n \t Pablo D�az-Heredero Garc�a \n \n \t \n [uc3m - 2018]");
				System.exit(0);
				break;

			}

			// ## LOOP DE PAUSA DEL DEL JUEGO [TAB] ## \\
			// ####################################### \\
			/*
			 * Con el fin de a�adir la posibilidad de pausar el juego, se ha introducido un
			 * bucle el cual se ejecuta siempre y cuando se haya activado la pausa. Una vez
			 * se desactiva la pausa contin�a la ejecuci�n normal del juego.
			 * 
			 * De esta manera se aprovecha el efecto que tiene un while en el loop del juego
			 * principal.
			 */
			while (trigger_gamePaused) {
				// ~~ Obtiene la acci�n de teclado en la pantalla de pausa ~~ \\
				String pauseAction = board.gb_getLastAction().trim();
				if (var_counter[7] < 1E7) {
					var_counter[7]++;
				} else {
					screenElement.animatePauseScreen(var_counter[8], true);
					if (var_counter[8] < 3) {
						var_counter[8]++;
					} else {
						var_counter[8] = 0;
					}
					var_counter[7] = 0;
				}
				if (pauseAction.equals("tab") || pauseAction.equals("command pause")) { // Si se pulsa tab o se
																						// introduce el comando
																						// de desactivaci�n de
																						// pausa
					screenElement.animatePauseScreen(var_counter[8], false);
					new sound.SoundLib("sound/sound_pause2.wav").start();
					trigger_gamePaused = false; // Se sale de la pantalla de pausa y contin�a la ejecuci�n del juego
				}
				if (pauseAction.equals("exit game")) {
					board.gb_showMessageDialog(
							"\t - GAME FINISHED - \n \n \t Pablo D�az-Heredero Garc�a \n \n \t \n [uc3m - 2018]");
					System.exit(0);
				}
			}

			// ## C�DIGO DE EJECUCI�N PERMANENTE ## \\
			// ## DURANTE LA EJECUCI�N DEL JUEGO ## \\
			/*
			 * Todo el c�digo que est� establecido a continuaci�n se ejecuta
			 * independientemente del nivel en el que se encuentre el jugador.
			 */
			// ## ACTUALIZACI�N DE LAS COORDENADAS Y SPRITES ## \\
			// ~~ Jugador ~~ \\
			board.gb_setSpriteImageDouble(pl.getID(), pl.getSprite());
			board.gb_moveSpriteCoord(pl.getID(), pl.getX(), pl.getY());

			// ~~ Enemigos ~~ \\
			for (int i = 0; i < enemies.length; i++) {
				for (int j = 0; j < enemies[i].length; j++) {
					if (enemies[i][j] != null) {
						// Si los enemigos han llegado a la formaci�n / Se encuentran en la formaci�n,
						// su X e Y es la de la formaci�n
						if (enemies[i][j].isInFormation() && trigger_enemiesReachedFormation) {
							enemies[i][j].setX(enemies[i][j].getFormationX());
							enemies[i][j].setY(enemies[i][j].getFormationY());

						}
						board.gb_setSpriteImage(enemies[i][j].getID(), enemies[i][j].getSprite());
						board.gb_moveSpriteCoord(enemies[i][j].getID(), enemies[i][j].getX(), enemies[i][j].getY());

					}
				}
			}

			// ## ANIMACIONES DE LOS SPRITES ## \\
			// ~~ Animaci�n del jugador [�ltima vida y da�o medio] ~~ \\

			if (var_counter[9] < 8) {
				var_counter[9]++;
			} else {
				if (!trigger_godModeActive && !trigger_bonusInvincibleActive && !pl.isInvencible()
						&& pl.getHealth() != 0) {
					pl.animateDamage();
				}
				var_counter[9] = 0;

			}
			// ~~ Animaci�n de la formaci�n [Iddle Animation] ~~ \\
			/*
			 * Solo se activa cuando el trigger de la animaci�n ha sido activado (cuando los
			 * enemigos han llegado a la formaci�n y han comenzado a moverse) y si no est�
			 * aplic�ndose la animaci�n de muerte. Adem�s, si los enemigos est�n congelados,
			 * la animaci�n se para hasta que estos se descongelen (de forma que se
			 * establecen los sprites de congelaci�n �nicamente).
			 * 
			 * Los cambios de sprite solo se aplican cuando el enemigo est� en la formaci�n
			 * (isInFormation), en caso de que haya saltado, estos cambios de sprite no se
			 * aplican y el enemigo obtiene el sprite mediante el m�todo (getSprite()) el
			 * cual le devuele el sprite asociado a su direcci�n actual.
			 */
			if (trigger_formationAnimated && !trigger_enemiesFreezed) {
				if (var_counter[10] < 5) {

					for (int i = 0; i < enemies.length; i++) {
						for (int j = 0; j < enemies[i].length; j++) {

							if (enemies[i][j] != null && enemies[i][j].isInFormation()
									&& !enemies[i][j].isDeadAnimation()) {
								switch (enemies[i][j].getType()) {
								case 'Z':
									board.gb_setSpriteImage(enemies[i][j].getID(), "enemy300.png");
									break;
								case 'G':
									board.gb_setSpriteImage(enemies[i][j].getID(), "enemy200.png");
									break;
								case 'C':
									if (enemies[i][j].getHealth() != 1) {
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemy1G0.png");
									} else {
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemy9G0.png");
									}
									break;
								case 'E':
									switch (enemies[i][j].getHealth()) {
									case 1:
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemyL300.png");
										break;
									case 2:
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemyL200.png");
										break;
									case 3:
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemyL00.png");
										break;
									}
								}
							}

						}
					}
					var_counter[10]++;
				} else if (var_counter[10] >= 5 && var_counter[10] < 10) {

					for (int i = 0; i < enemies.length; i++) {
						for (int j = 0; j < enemies[i].length; j++) {

							if (enemies[i][j] != null && enemies[i][j].isInFormation()
									&& !enemies[i][j].isDeadAnimation()) {
								switch (enemies[i][j].getType()) {
								case 'Z':
									board.gb_setSpriteImage(enemies[i][j].getID(), "enemy3G0.png");
									break;
								case 'G':
									board.gb_setSpriteImage(enemies[i][j].getID(), "enemy2G0.png");
									break;
								case 'C':
									if (enemies[i][j].getHealth() != 1) {
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemy1G1.png");
									} else {
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemy9G1.png");
									}
									break;
								case 'E':
									switch (enemies[i][j].getHealth()) {
									case 1:
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemyL3G1.png");
										break;
									case 2:
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemyL2G1.png");
										break;
									case 3:
										board.gb_setSpriteImage(enemies[i][j].getID(), "enemyLG1.png");
										break;
									}
									break;
								}

							}

						}
					}

					var_counter[10]++;

				} else {
					var_counter[10] = 0;
				}
			}

			// ~~ Animaci�n de Muerte del JUGADOR ~~ //
			if (pl.isDeadAnimation()) {
				pl.setAnimationCounter(pl.getAnimationCounter() + 1);
				if (pl.getAnimationCounter() < 5) {
					pl.setSprite("explosion11.png");
					if (trigger_soundsActive) {
						new sound.SoundLib("sound/sound_explosion.wav").start();
					}
				} else if (pl.getAnimationCounter() >= 5 && pl.getAnimationCounter() < 10) {
					pl.setSprite("explosion12.png");
				} else if (pl.getAnimationCounter() >= 10 && pl.getAnimationCounter() < 15) {
					pl.setSprite("explosion13.png");
				} else if (pl.getAnimationCounter() >= 15 && pl.getAnimationCounter() < 17) {
					pl.setSprite("explosion14.png");
				} else if (pl.getAnimationCounter() >= 17 && pl.getAnimationCounter() < 19) {
					pl.setSprite("explosion15.png");
				} else if (pl.getAnimationCounter() >= 19 && pl.getAnimationCounter() < 20) {
					pl.setSprite("explosion16.png");
				} else if (pl.getAnimationCounter() >= 20 && pl.getAnimationCounter() < 21) {
					pl.setSprite("explosion17.png");
				} else if (pl.getAnimationCounter() >= 21) {
					pl.setSprite("explosion18.png");
					board.gb_setSpriteVisible(pl.getID(), false);
				}
			}
			// ~~ Animaci�n de Muerte de los ENEMIGOS ~~ //
			for (int i = 0; i < enemies.length; i++) {
				for (int j = 0; j < enemies[i].length; j++) {
					if (enemies[i][j] != null) {

						if (enemies[i][j].isDeadAnimation()) {
							enemies[i][j].setAnimationCounter(enemies[i][j].getAnimationCounter() + 3);
							if (enemies[i][j].getAnimationCounter() < 3) {
								if (trigger_enemiesFreezed) {
									enemies[i][j].setSprite("explosion20freeze.png");
								} else {
									enemies[i][j].setSprite("explosion20.png");
								}
							}
							if (enemies[i][j].getAnimationCounter() >= 3 && enemies[i][j].getAnimationCounter() < 6) {
								if (trigger_enemiesFreezed) {
									enemies[i][j].setSprite("explosion21freeze.png");
								} else {
									enemies[i][j].setSprite("explosion21.png");
								}
							}
							if (enemies[i][j].getAnimationCounter() >= 6 && enemies[i][j].getAnimationCounter() < 9) {
								if (trigger_enemiesFreezed) {
									enemies[i][j].setSprite("explosion22freeze.png");
								} else {
									enemies[i][j].setSprite("explosion22.png");
								}
							}
							if (enemies[i][j].getAnimationCounter() >= 9 && enemies[i][j].getAnimationCounter() < 12) {
								if (trigger_enemiesFreezed) {
									enemies[i][j].setSprite("explosion23freeze.png");
								} else {
									enemies[i][j].setSprite("explosion23.png");
								}
							}
							if (enemies[i][j].getAnimationCounter() >= 12 && enemies[i][j].getAnimationCounter() < 15) {
								if (trigger_enemiesFreezed) {
									enemies[i][j].setSprite("explosion24freeze.png");
								} else {
									enemies[i][j].setSprite("explosion24.png");
								}
							}
							if (enemies[i][j].getAnimationCounter() >= 15) {
								board.gb_setSpriteVisible(enemies[i][j].getID(), false);
								enemies[i][j].setDeadAnimation(false);
							}
						}
					}

				}
			}

			// ## MARCADOR DE PUNTUACI�N GR�FICO EN EL TABLERO ## \\
			screenElement.getDigits(pl.getScore()); // Este m�todo muestra los sprites de los n�meros dependiendo de
													// la puntuaci�n actual del jugador

			// ## REPRODUCCI�N DEL SOUNDTRACK DEL JUEGO ## \\
			if (var_counter[1] < 10) { // Contador de reproducci�n �nica
				if (var_counter[1] == 8) {
					if (trigger_soundsActive) {
						new sound.SoundLib("sound/sound_soundTrack1.wav").start();
					}
				}
				var_counter[1]++;
			} else {
				var_counter[1] = 10;
			}

			// ## BONUS DEL JUEGO ## // [Se activan con la tecla UP]
			// ~~ [CONGELACI�N] 2000 , 10000, 20000 Puntos ~~
			if (pl.getScore() >= 2000 && var_bonusCounter == 0 || pl.getScore() >= 10000 && var_bonusCounter == 2
					|| pl.getScore() >= 20000 && var_bonusCounter == 4) {
				trigger_bonusActive = true; // Activa
											// el
											// bonus
				if (pl.getScore() >= 2000 && pl.getScore() < 10000) {
					screenElement.showText("Bonus1", true); // Texto del bonus
				} else if (pl.getScore() >= 10000 && pl.getScore() < 20000) {
					screenElement.showText("Bonus3", true);
				} else {
					screenElement.showText("Bonus5", true);
				}
				screenElement.showBonusImage("Bonus1Small", true); // Im�gen
				// inferior
				// derecha
				// del
				// bonus
				if (trigger_soundsActive) {
					new sound.SoundLib("sound/sound_bonus.wav").start();
				}
				var_counter[11] = 0;
				trigger_textShowing = true;
				var_bonusCounter++;
			}

			if (var_counter[11] < 50 && trigger_textShowing) {
				var_counter[11]++;
			} else if (trigger_textShowing) {
				screenElement.hideText(); // Oculta el texto de Bonus 3

				trigger_textShowing = false;
				var_counter[11] = 0;

			}
			// ~~ [INVENCIBILIDAD] 6000 , 14000, 25000 Puntos ~~
			if (pl.getScore() >= 6000 && var_bonusCounter == 1 || pl.getScore() >= 14000 && var_bonusCounter == 3
					|| pl.getScore() >= 25000 && var_bonusCounter == 5) {
				trigger_bonusActive = true; // Activa
											// el
											// bonus
				if (pl.getScore() >= 6000 && pl.getScore() < 14000) {
					screenElement.showText("Bonus2", true); // Texto del bonus
				} else if (pl.getScore() >= 14000 && pl.getScore() < 25000) {
					screenElement.showText("Bonus4", true);
				} else {
					screenElement.showText("Bonus6", true);
				}
				screenElement.showBonusImage("Bonus2Small", true); // Im�gen inferior derecha del bonus

				if (trigger_soundsActive) {
					new sound.SoundLib("sound/sound_bonus.wav").start();
				}
				var_counter[11] = 0;
				trigger_textShowing = true;
				var_bonusCounter++;
			}

			if (var_counter[11] < 50 && trigger_textShowing) {
				var_counter[11]++;
			} else if (trigger_textShowing) {

				screenElement.hideText(); // Oculta el texto de Bonus 4

				trigger_textShowing = false;
				var_counter[11] = 0;

			}

			// ## VIDA, DA�O E INVENCIBILIDAD EN EL JUGADOR ## \\
			// ~~ Comprobaci�n de da�o al jugador ~~ \\
			for (int i = 0; i < enemies.length; i++) {
				for (int j = 0; j < enemies[i].length; j++) {
					// ** Da�o cuando se chocan los enemigos contra el ** \\
					if (enemies[i][j] != null) {

						if (((pl.getY() >= enemies[i][j].getY() - Constants.HITBOXSIZE)
								&& (pl.getY() <= enemies[i][j].getY() + Constants.HITBOXSIZE))
								&& (pl.getX() >= enemies[i][j].getX() - Constants.HITBOXSIZE)
								&& (pl.getX() <= enemies[i][j].getX() + Constants.HITBOXSIZE)
								&& enemies[i][j].getHealth() != 0) {
							if (var_counter[6] == 0) { // Este contador a�ade un retardo en el cual eres invencible
								pl.setHealth(0); // Establece la vida a 0 si se chocan los enemigos contra el
													// jugador

							}

						}

					}
					// ** Da�o cuando recibe un disparo de un enemigo ** \\
					if (enemies[i][j] != null) {
						for (int s = 0; s < enemies[i][j].shot.length; s++) {
							if (enemies[i][j].shot[s] != null) {

								if (((pl.getY() >= enemies[i][j].shot[s].getY() - Constants.HITBOXSIZE)
										&& (pl.getY() <= enemies[i][j].shot[s].getY() + Constants.HITBOXSIZE))
										&& (pl.getX() >= enemies[i][j].shot[s].getX() - Constants.HITBOXSIZE)
										&& (pl.getX() <= enemies[i][j].shot[s].getX() + Constants.HITBOXSIZE)
										&& enemies[i][j].getHealth() != 0 && !enemies[i][j].shot[s].isOut()) {

									if (var_counter[6] == 0) { // Este contador a�ade un retardo en el cual eres
										// invencible
										enemies[i][j].shot[s].setOut(true);
										trigger_shotReceived = true;

									}

								}
							}

						}

					}
				}
			}

			// ~~ Recepci�n de un disparo enemigo ~~ \\
			if (pl.getHealth() != 0 && trigger_shotReceived) {

				if (var_counter[6] == 0) { // Este contador a�ade un retardo en el cual eres invencible
					if (pl.getHealth() != 0) {
						pl.setHealth(pl.getHealth() - 1);
						if (trigger_soundsActive && pl.getHealth() != 0) {
							new sound.SoundLib("sound/sound_damage.wav").start();
						}
					}
					board.gb_animateDamage();
					pl.setInvencible(true); // Establece al jugador en modo invencible durante un corto
											// periodo
					// de tiempo para evitar que un solo enemigo le quite toda la vida
					trigger_shotReceived = false;
				}

			}

			// ~~ Comprobaci�n de la vida del jugador | Representaci�n gr�fica de las vidas
			// ~~ \\
			if (pl.getHealth() < Constants.MAXHEALTH) {
				for (int i = 0; i < Constants.MAXHEALTH; i++) {
					if (pl.getHealth() == Constants.MAXHEALTH - i) {
						board.gb_setSpriteImage((2 + Constants.MAXHEALTH) - i, "heartOut.png", 40, 40);
						// Si se pierde una vida, el �ltimo corazon en rojo se convierte en un coraz�n
						// en blanco y negro
						if (pl.getHealth() == Constants.MAXHEALTH - (Constants.MAXHEALTH - 1)) {
							board.gb_animateDamage(); // Cuando queda una vida muestra una imagen de peligro encima
														// del retrato del jugador
						} else if (pl.getHealth() == 0) {
							screenElement.hideText(); // Esconde todos los textos
						}
					}
				}
			} else {
				for (int i = 0; i < Constants.MAXHEALTH; i++) {
					board.gb_setSpriteImage(2 + i, "heart.png", 40, 40);
					// Establece de nuevo todas las vidas (Si se pulsa nuevo juego)

				}
			}
			// ~~ Comprobaci�n de muerte de jugador //
			if (pl.getHealth() <= 0) {
				for (int i = 0; i < Constants.MAXHEALTH; i++) {
					board.gb_setSpriteImage(2 + i, "heartOut.png", 40, 40); // Establece todos los corazones en
																			// blanco y negro.
				}

				pl.setDeadAnimation(true); // Establece la animaci�n de muerte del jugador
				screenElement.showText("GameOver", true); // Muestra el texto de muerte
				if (var_counter[12] < 50) {
					var_counter[12]++;
				} else {
					var_counter[12] = 0;
					trigger_gameRunning = false;

				}
			}

			// ~~ Periodo de invencibilidad del jugador [Da�o recibido] ~~ \\
			if (pl.isInvencible() && !trigger_godModeActive && !trigger_bonusInvincibleActive) {
				if (var_counter[6] < 40) {
					var_counter[6]++;
					if (pl.getHealth() > 0) {
						pl.setSprite("playerGod.png");
						screenElement.hideText(); // Oculta los textos anteriores
						screenElement.showTextDamage(true);
					}
				} else {
					pl.setSprite("player.png");
					screenElement.showTextDamage(false);
					pl.setInvencible(false);
					var_counter[6] = 0;
				}
			}

			// ~~ Bonus de invencibilidad activado ~~ \\
			if (pl.isInvencible() && !trigger_godModeActive && trigger_bonusInvincibleActive) {
				if (var_counter[6] < 100) {
					var_counter[6]++;
					if (pl.getHealth() != 0) { // Evita
						// que
						// se
						// actualicen
						// la
						// animaci�n
						// de
						// muerte
						// junto
						// al
						// sprite
						// de
						// invencibilidad
						pl.setSprite("playerGod.png");
						screenElement.showText("BonusInvincible", true);
					}
				}
				if (var_counter[6] == 100) {
					var_counter[6] = 0;
					pl.setSprite("player.png");
					screenElement.hideText(); // Esconde el texto de Bonus de invencibilidad
					pl.setInvencible(false);
					trigger_bonusInvincibleActive = false;
				}
			}

			// ## ACTUALIZACI�N DE LOS ATRIBUTOS DEL TABLERO ## //
			board.gb_setValuePointsUp(pl.getScore());
			board.gb_setValuePointsDown(pl.getSpeed());
			board.gb_setValueLevel(var_actualLevel);
			board.gb_setValueAbility2(pl.getEnemyHit());
			board.gb_setValueAbility1(pl.getShotCount());
			board.gb_setValueHealthMax(Constants.MAXHEALTH);
			board.gb_setValueHealthCurrent(pl.getHealth());

			// ## IMAGEN DE INICIO ## //
			// ~~ Muestra la imagen de inicio ~~ //
			if (var_counter[13] < 70 && !trigger_welcomeShowed) {
				screenElement.showText("Welcome", true);
				var_counter[13]++;
			} else if (!trigger_welcomeShowed && var_counter[13] == 70) {
				screenElement.hideText(); // Oculta los textos
				trigger_welcomeShowed = true;
				var_counter[13] = 0;
			}

			// ## BONUS ## //
			// ~~ Descongelaci�n de los enemigos tras haber activado el bonus ~~ \\
			if (trigger_enemiesFreezed) {
				if (var_counter[14] < 80) {
					var_counter[14]++;
				} else {
					var_counter[14] = 0;
					trigger_enemiesFreezed = false;
					for (int i = 0; i < enemies.length; i++) {
						for (int j = 0; j < enemies[i].length; j++) {
							if (enemies[i][j] != null) {
								enemies[i][j].setFreezed(false);
								board.gb_setSpriteImage(enemies[i][j].getID(), enemies[i][j].getSprite());
							}
						}
					}
					if (trigger_soundsActive) {
						new sound.SoundLib("sound/sound_freeze.wav").start();
					}
					screenElement.hideText(); // Oculta el texto de enemigos congelados
				}
			}

			// ~~ Bonus de invencibilidad del jugador ~~ \\
			if (!trigger_bonusActive && !trigger_godModeActive && pl.isInvencible() && var_counter[15] < 2000) {
				/*
				 * Si el modo dios est� activado nunca se quitar� la invencibilidad
				 */
				var_counter[15]++;
			} else if (var_counter[15] == 2000 && !trigger_bonusActive && !trigger_godModeActive) {
				var_counter[15] = 0;
				pl.setInvencible(false);
			}

			// ## SALTO ALEATORIO DE LOS ENEMIGOS DE LA FORMACI�N ## \\
			if (trigger_enemiesReachedFormation) {
				// ~~ Establecimiento aleatorio del salto de la formaci�n y del disparo ~~ \\
				for (int i = 0; i < enemies.length; i++) {
					for (int j = 0; j < enemies[i].length; j++) {
						if (enemies[i][j] != null) {
							if (var_actualLevel != 0) {
								if (Math.random() * 100 < Constants.PROBJUMPLV[var_actualLevel - 1]
										&& !trigger_enemiesFreezed) {

									enemies[i][j].setInFormation(false);
									// Hace que el enemigo salte de la formaci�n
								}
								if (Math.random() * 100 < Constants.PROBSHOTLV[var_actualLevel - 1]
										&& !trigger_enemiesFreezed && !enemies[i][j].isInFormation()
										&& !enemies[i][j].isOutPath()) {

									// M�todo startShot, activa la adici�n de un disparo en el enemigo
									enemies[i][j].startShot(board);

									if (trigger_soundsActive && enemies[i][j].startShot(board)) {
										switch (enemies[i][j].getType()) {
										case 'E':
											new sound.SoundLib("sound/sound_enemyEnterpriseShot.wav").start();
											break;
										default:
											new sound.SoundLib("sound/sound_enemyShot.wav").start();
										}
									}
								}
							}
							if (enemies[i][j].isInFormation()) {
								// Si se encuentra en la formaci�n, la X y la Y son las de la formaci�n
								enemies[i][j].setX(enemies[i][j].getX());
								enemies[i][j].setY(enemies[i][j].getY());

							}

						}
					}
				}
				// ~~ Seguimiento de un camino seleccionado aleatoriamente si no est� en la
				// formaci�n el enemigo ~~ \\
				for (int i = 0; i < enemies.length; i++) {
					for (int j = 0; j < enemies[i].length; j++) {
						if (enemies[i][j] != null) {
							if (!enemies[i][j].isInFormation()) {
								if (!trigger_enemiesFreezed) {
									if (!enemies[i][j].isOutPath()) {
										if (enemies[i][j].getProbabilityCounter() < 100) {
											enemies[i][j].upProbCounter(); // Incrementa el contador en una unidad. Este
																			// contador generar� un nuevo camino cuando
																			// haya llegado a 100
										} else if (enemies[i][j].getProbabilityCounter() == 100) {
											enemies[i][j].setRandomPath((int) (Math.random() * 6)); // Genera un n�mero
																									// aleatorio que
																									// determina el
																									// camino a seguir
																									// hasta que se
																									// genere uno nuevo
											enemies[i][j].resetProbCounter(); // Reinicia el contador a 0
											enemies[i][j].resetPath(); // Reinicia el indexPath y el counterPath
										}
										// Selecci�n del camino dependiendo del n�mero aleatorio generado
										if (enemies[i][j].getType() != 'E') {
											switch (enemies[i][j].getRandomPath()) {

											case 0:
												enemies[i][j].followPath(Constants.PATHCIRCLEANTICLOCKWISE);
												break;
											case 1:
												enemies[i][j].followPath(Constants.PATHCIRCLECLOCKWISE);
												break;
											case 2:
												enemies[i][j].followPath(Constants.PATHZIGZAGLEFTBIG);
												break;
											case 3:
												enemies[i][j].followPath(Constants.PATHZIGZAGRIGHTBIG);
												break;
											case 4:
												enemies[i][j].followPath(Constants.PATHZIGZAGLEFT);
												break;
											case 5:
												enemies[i][j].followPath(Constants.PATHZIGZAGRIGHT);
												break;
											}
										} else {
											switch (enemies[i][j].getRandomPath()) {

											case 0:
											case 5:
												enemies[i][j].followPath(Constants.PATHENTERPRISELEFT);
												break;
											case 1:
											case 3:
												enemies[i][j].followPath(Constants.PATHENTERPRISERIGHT);
												break;
											default:
												enemies[i][j].move(Constants.DIR_S, 1);

											}
										}

									}

									board.gb_setSpriteImage(enemies[i][j].getID(), enemies[i][j].getSprite());
									board.gb_moveSpriteCoord(enemies[i][j].getID(), enemies[i][j].getX(),
											enemies[i][j].getY());

									// Si los enemigos se salen del tablero por la parte inferior, se establece como
									// que han salido del tablero mediante OutPath
									if (enemies[i][j].getY() > 220) {
										enemies[i][j].setY(-20);
										enemies[i][j].setOutPath(true);
									}

									// Si los enemigos se han salido por el tablero (OutPath == true) los enemigos
									// conservan su coordenada X y bajan hacia llegar a su coordenada Y
									// correspondiente en la formaci�n. Tambi�n se desplazar�n hasta que su
									// coordenada X coincida con la de la formaci�n.
									if (enemies[i][j].isOutPath()) {
										if (enemies[i][j].getY() < enemies[i][j].getFormationY() - 5
												&& enemies[i][j].getY() < enemies[i][j].getFormationY() + 5) {
											enemies[i][j].move(Constants.DIR_S, 1); // Se desplaza hacia abajo hasta
																					// llegar a la Y de la formaci�n
										} else if (enemies[i][j].getX() < enemies[i][j].getFormationX() - 5
												&& enemies[i][j].getX() < enemies[i][j].getFormationX() + 5) {
											enemies[i][j].move(Constants.DIR_E, 1); // Desplazamiento lateral hasta
																					// llegar a la X de la formaci�n

										} else if (enemies[i][j].getX() > enemies[i][j].getFormationX() - 5
												&& enemies[i][j].getX() > enemies[i][j].getFormationX() + 5) {
											enemies[i][j].move(Constants.DIR_W, 1); // Desplazamiento lateral hasta
																					// llegar a la X de la formaci�n

										} else {
											// Una vez han llegado a su posici�n en la formaci�n, se establecen en la
											// formaci�n (InFormation = true) hasta que aleatoriamente puedan saltar de
											// nuevo
											enemies[i][j].setX(enemies[i][j].getFormationX());
											enemies[i][j].setY(enemies[i][j].getFormationY());
											enemies[i][j].setDirection(Constants.DIR_N);
											enemies[i][j].setInFormation(true);
											enemies[i][j].setOutPath(false);
											/*
											 * NOTA: En caso de encontrarse el jugador en modo dios y llegar la
											 * formaci�n hasta el final del tablero, por como se ha construido el
											 * movimiento de vuelta a la formaci�n (OutPath), los enemigos comenzar�n a
											 * descender desde la parte superior hasta la parte inferior del nivel de
											 * forma indefinida mientras sigan con vida.
											 * 
											 */

										}
									}
								}
							}
						}
					}
				}
			}

			// ## PASO DE NIVEL ## \\
			// ~~ Reinicio de las variables y triggers al pasar de nivel ~~ \\
			if (trigger_levelCompleted) {
				// ~~ Eliminaci�n de todos los enemigos y de sus disparos una vez se ha
				// completado el nivel ~~ \\
				for (int i = 0; i < enemies.length; i++) {
					for (int j = 0; j < enemies[i].length; j++) {
						if (enemies[i][j] != null) {
							for (int s = 0; s < enemies[i][j].shot.length; s++) {

								if (enemies[i][j].shot[s] != null) {
									board.gb_setSpriteVisible(enemies[i][j].shot[s].getID(), false);
								}

							}
							board.gb_setSpriteVisible(enemies[i][j].getID(), false);
							enemies[i][j] = null;
						}
					}
				}
				
				// Reinicio de trigggers, variables y contadores
				trigger_enemiesReachedFormation = false;
				trigger_formationReachedMax = false;
				trigger_levelGenerated = false;
				trigger_allEnemyDead = false;
				trigger_levelStart = false;
				var_shotCounter = 0; 
				var_zakoCounter = 0; 
				var_goeiCounter = 0;
				var_commanderCounter = 0; 
				var_enterpriseCounter = 0;
				var_killedEnemies = 0;
				var_counter[17] = 0;
				var_counter[16] = 0;
				if (!trigger_levelCommandIntroduced) { // Si no se ha introducido el comando de nivel
					do { // LOOP que muestra el mensaje de nivel completado
						var_counter[17]++;
						screenElement.showText("LevelCompleted", true);
						Thread.sleep(30L);
					} while (var_counter[17] < 50);
					screenElement.showText("LevelCompleted", false);

					if (var_actualLevel < 4) {
						var_actualLevel += 1; // Aumenta el nivel en 1
					} else {
						trigger_gameRunning = false; // Finaliza el juego
					}
				}
				trigger_levelCommandIntroduced = false;
				trigger_levelCompleted = false;

			}

			// ## MOVIMIENTO DE LA FORMACI�N ## \\
			/*
			 * Utilizamos un contador que determina cuando la formaci�n debe cambiar de
			 * direcci�n de movimiento. En primer lugar la formaci�n se posiciona en el
			 * centro del nivel por lo que el contador [en estado inicial] hace que los
			 * enemigos se muevan hacia la derecha hasta que llega a 30. En ese momento, el
			 * contador pasa del estado inicial al estado final, en el cual cuenta hasta 60
			 * (ya que si recorrer desde el centro hasta el m�ximo que hemos determinado
			 * lleva 30 cuentas, recorrer desde el m�nimo que hemos determinado hasta ese
			 * m�ximo llevar� 60 [tablero completo]).
			 * 
			 * Al cambiar de direcci�n todos los enemigos en la formaci�n aumentan su
			 * coordenada Y de la formaci�n en 1 unidad, desplaz�ndolos hacia abajo muy
			 * lentamente.
			 * 
			 * 
			 */
			if (trigger_enemiesReachedFormation && !trigger_enemiesFreezed) {
				if (var_counter[16] >= 31 && var_counter[16] < 91 || var_counter[16] < 30) {
					var_counter[16]++; // Este contador controla el movimiento de la formaci�n
				} else if (var_counter[16] >= 31 && var_counter[16] == 91 || var_counter[16] == 30) {
					if (trigger_formationReachedMax) {
						trigger_formationReachedMax = false;
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								if (enemies[i][j] != null) {
									enemies[i][j].setFormationY(enemies[i][j].getFormationY() + 1);
								}
							}
						}
					} else {
						trigger_formationReachedMax = true;
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								if (enemies[i][j] != null) {
									enemies[i][j].setFormationY(enemies[i][j].getFormationY() + 1);
								}
							}
						}
					}
					var_counter[16] = 31;

				}

				for (int i = 0; i < enemies.length; i++) {
					for (int j = 0; j < enemies[i].length; j++) {
						if (enemies[i][j] != null) {
							if (!trigger_formationReachedMax) {
								enemies[i][j].setFormationX(enemies[i][j].getFormationX() + 1);

							} else {
								enemies[i][j].setFormationX(enemies[i][j].getFormationX() - 1);
							}

						}
					}

				}

			}

			// ## ENTRADA DE LOS ENEMIGOS ## //
			/*
			 * Las entradas de los enemigos est�n definidas por fases. Cada enemigo tiene
			 * una fase de entrada individual que se actualiza seg�n va llegando a los
			 * puntos que se han designado. Una vez llega, salta a la siguiente fase. Cuando
			 * todos llegan a la �ltima fase, la formaci�n se establece y comienza a
			 * moverse.
			 * 
			 * Cuando a�adimos los enemigos al tablero utilizamos un contador de enemigos
			 * para determinar su distribuci�n homog�nea (mediante el m�dulo % 2 y la
			 * paridad del n�mero de enemigo). En el caso de las entradas, para dividir a
			 * los enemigos en grupos de dos y que entren por lados opuestos, se ha
			 * utilizado la paridad de las ID's de los enemigos y el m�dulo % 2, ya que las
			 * ID's identifican de forma �nica e individual a los enemigos, y al ser
			 * secuenciales, han sido de mucha utilidad a la hora de dividir a los enemigos
			 * en grupos homog�neos.
			 * 
			 */
			if (trigger_levelStart) {
				if (!trigger_enemiesFreezed) { // Si los enemigos est�n congelados se pausar� el movimiento
					for (int i = 0; i < enemies.length; i++) {
						for (int j = 0; j < enemies[i].length; j++) {
							if (enemies[i][j] != null) {

								if (enemies[i][j].getEntryPhase() == 8) { // Comprueba que los enemigos hayan llegado a
																			// la �ltima fase de su movimiento de
																			// entrada

									trigger_formationAnimated = true; // Activa la animaci�n de los sprites

									// de
									// los enemigos
									trigger_enemiesReachedFormation = true; // Activa el movimiento de la
																			// formaci�n
									// de enemigos

								} else {
									trigger_formationAnimated = false; // Activa la animaci�n de los sprites
																		// de
									// los enemigos
									trigger_enemiesReachedFormation = false; // Activa el movimiento de la
									// formaci�n
									// de enemigos
								}

								// ~~ Entrada de los Goeis ~~ \\
								if (enemies[i][j].getType() == 'G') {
									switch (enemies[i][j].getEntryPhase()) {
									case 0:
										if (enemies[i][j].getY() > 110) {
											enemies[i][j].move(Constants.DIR_N, 1);
										} else {
											enemies[i][j].setEntryPhase(1);
										}
										break;
									case 1:
										if (enemies[i][j].getID() % 2 == 0) {
											if (enemies[i][j].getX() < 105) {
												enemies[i][j].move(Constants.DIR_NE, 1);
											} else {
												enemies[i][j].setEntryPhase(2);
											}

										} else {
											if (enemies[i][j].getX() > 65) {
												enemies[i][j].move(Constants.DIR_NW, 1);
											} else {
												enemies[i][j].setEntryPhase(2);
											}
										}
										break;
									case 2:
										/* [CLOCKWISE / ANTICLOCKWISE Circles]
										 * Directions: 1 Thru 16 e mod 16 | e % 16 --> r < 16
										 * 
										 * move(DIRECTION, STEPS) move(enemies[i][j].getDirection() + 1 % 16, 1) -
										 * Clockwise Ex: 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
										 * move(enemies[i][j].getDirection() + 15 % 16, 1 - Anticlockwise Spin Ex: 0 15
										 * 14 13 12 11 10 9 8 7 6 5 4 3 2 1
										 */

										if (enemies[i][j].getID() % 2 == 0 && enemies[i][j].getEntryCircle() != 1) {
											enemies[i][j].move((enemies[i][j].getDirection() + 1) % 16, 2);
											if (enemies[i][j].getDirection() == 15) {
												enemies[i][j].setEntryCircle(enemies[i][j].getEntryCircle() + 1);
											}
										} else if (enemies[i][j].getID() % 2 == 0) {
											enemies[i][j].setEntryPhase(3);
											enemies[i][j].setEntryCircle(0);

										}
										if (enemies[i][j].getID() % 2 != 0 && enemies[i][j].getEntryCircle() != 1) {
											enemies[i][j].move((enemies[i][j].getDirection() + 15) % 16, 2);
											if (enemies[i][j].getDirection() == 0) {
												enemies[i][j].setEntryCircle(enemies[i][j].getEntryCircle() + 1);
											}
										} else if (enemies[i][j].getID() % 2 != 0) {
											enemies[i][j].setEntryPhase(3);
											enemies[i][j].setEntryCircle(0);

										}
										break;
									case 3:
										if (enemies[i][j].getY() > 35) {
											enemies[i][j].move(Constants.DIR_N, 1);
										} else {
											enemies[i][j].setEntryPhase(4);
										}
										break;
									case 4:
										if (enemies[i][j].getID() % 2 == 0) {
											if (enemies[i][j].getX() < 120) {
												enemies[i][j].move(Constants.DIR_E, 1);
											} else {
												enemies[i][j].setEntryPhase(5);
											}
										} else {
											if (enemies[i][j].getX() > 50) {
												enemies[i][j].move(Constants.DIR_W, 1);
											} else {
												enemies[i][j].setEntryPhase(5);
											}
										}
										break;
									case 5:
										if (enemies[i][j].getY() > 20) {
											enemies[i][j].move(Constants.DIR_N, 1);
										} else {
											enemies[i][j].setEntryPhase(6);
										}

										break;
									case 6:
										if (enemies[i][j].getX() < enemies[i][j].getFormationX()) {
											enemies[i][j].setX(enemies[i][j].getX() + 1);

										} else if (enemies[i][j].getX() > enemies[i][j].getFormationX()) {

											enemies[i][j].setX(enemies[i][j].getX() - 1);
										}
										if (enemies[i][j].getY() < enemies[i][j].getFormationY()) {
											enemies[i][j].setY(enemies[i][j].getY() + 1);

										} else if (enemies[i][j].getY() > enemies[i][j].getFormationY()) {

											enemies[i][j].setY(enemies[i][j].getY() - 1);
										}
										if (enemies[i][j].getX() == enemies[i][j].getFormationX()
												&& enemies[i][j].getY() == enemies[i][j].getFormationY()) {
											enemies[i][j].setEntryPhase(7);
										}
										enemies[i][j].setDirection(Constants.DIR_N);

										break;
									case 7:
										enemies[i][j].setEntryPhase(8);
										break;

									}

								}

								// ~~ Entrada de los Zakos ~~ \\
								if (enemies[i][j].getType() == 'Z') {
									switch (enemies[i][j].getEntryPhase()) {
									case 0:
										if (enemies[i][j].getID() % 2 == 0) {

											if (enemies[i][j].getX() < 150) {
												enemies[i][j].move(Constants.DIR_E, 1);
											} else {
												enemies[i][j].setEntryPhase(1);
											}

										} else {
											if (enemies[i][j].getX() > 20) {
												enemies[i][j].move(Constants.DIR_W, 1);
											} else {
												enemies[i][j].setEntryPhase(1);
											}
										}
										break;
									case 1:
										/*
										 * Directions: 1 Thru 16 e mod 16 | e % 16 --> r < 16
										 * 
										 * move(DIRECTION, STEPS) move(enemies[i][j].getDirection() + 1 % 16, 1) -
										 * Clockwise Ex: 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
										 * move(enemies[i][j].getDirection() + 15 % 16, 1 - Anticlockwise Spin Ex: 0 15
										 * 14 13 12 11 10 9 8 7 6 5 4 3 2 1
										 */

										if (enemies[i][j].getID() % 2 == 0 && enemies[i][j].getEntryCircle() != 3) {
											enemies[i][j].move((enemies[i][j].getDirection() + 1) % 16, 1);
											if (enemies[i][j].getDirection() == 15) {
												enemies[i][j].setEntryCircle(enemies[i][j].getEntryCircle() + 1);
											}
										} else if (enemies[i][j].getID() % 2 == 0) {
											enemies[i][j].setEntryPhase(2);
											enemies[i][j].setEntryCircle(0);
										}
										if (enemies[i][j].getID() % 2 != 0 && enemies[i][j].getEntryCircle() != 3) {
											enemies[i][j].move((enemies[i][j].getDirection() + 15) % 16, 1);
											if (enemies[i][j].getDirection() == 0) {
												enemies[i][j].setEntryCircle(enemies[i][j].getEntryCircle() + 1);
											}
										} else if (enemies[i][j].getID() % 2 != 0) {
											enemies[i][j].setEntryPhase(2);
											enemies[i][j].setEntryCircle(0);

										}
										break;
									case 2:
										if (enemies[i][j].getY() > 110) {
											enemies[i][j].move(Constants.DIR_N, 1);
										} else {
											enemies[i][j].setEntryPhase(3);
										}

										break;
									case 3:
										if (enemies[i][j].getID() % 2 == 0) {

											if (enemies[i][j].getX() > 90) {
												enemies[i][j].move(Constants.DIR_NW, 1);
											} else {
												enemies[i][j].setEntryPhase(4);
											}

										} else {
											if (enemies[i][j].getX() < 70) {
												enemies[i][j].move(Constants.DIR_NE, 1);
											} else {
												enemies[i][j].setEntryPhase(4);
											}
										}
										break;
									case 4:
										enemies[i][j].setDirection(Constants.DIR_N);
										enemies[i][j].setEntryPhase(6);

										break;
									case 6:

										if (enemies[i][j].getX() < enemies[i][j].getFormationX()) {
											enemies[i][j].setX(enemies[i][j].getX() + 1);

										} else if (enemies[i][j].getX() > enemies[i][j].getFormationX()) {

											enemies[i][j].setX(enemies[i][j].getX() - 1);
										}
										if (enemies[i][j].getY() < enemies[i][j].getFormationY()) {
											enemies[i][j].setY(enemies[i][j].getY() + 1);

										} else if (enemies[i][j].getY() > enemies[i][j].getFormationY()) {

											enemies[i][j].setY(enemies[i][j].getY() - 1);
										}
										if (enemies[i][j].getX() == enemies[i][j].getFormationX()
												&& enemies[i][j].getY() == enemies[i][j].getFormationY()) {
											enemies[i][j].setEntryPhase(7);
										}
										break;
									case 7:
										enemies[i][j].setEntryPhase(8);

										break;

									}
								}

								// ~~ Entrada de los Comandantes G�laga ~~ \\
								if (enemies[i][j].getType() == 'C') {
									switch (enemies[i][j].getEntryPhase()) {
									case 0:
										if (enemies[i][j].getY() < 135) {
											enemies[i][j].move(Constants.DIR_S, 1);
										} else {
											enemies[i][j].setEntryPhase(1);
										}
										break;
									case 1:
										if (enemies[i][j].getID() % 2 == 0) {
											if (enemies[i][j].getX() > 40) {
												enemies[i][j].move(Constants.DIR_W, 1);

											} else {
												enemies[i][j].setEntryPhase(2);

											}
										} else {
											if (enemies[i][j].getX() < 130) {
												enemies[i][j].move(Constants.DIR_E, 1);

											} else {
												enemies[i][j].setEntryPhase(2);

											}
										}

										break;
									case 2:
										if (enemies[i][j].getID() % 2 == 0) {
											if (enemies[i][j].getX() > 10) {
												enemies[i][j].move(Constants.DIR_NW, 1);

											} else {
												enemies[i][j].setEntryPhase(3);

											}
										} else {
											if (enemies[i][j].getX() < 160) {
												enemies[i][j].move(Constants.DIR_NE, 1);

											} else {
												enemies[i][j].setEntryPhase(3);

											}
										}

										break;
									case 3:
										if (enemies[i][j].getID() % 2 == 0) {
											if (enemies[i][j].getX() < 80) {
												enemies[i][j].move(Constants.DIR_NE, 1);

											} else {
												enemies[i][j].setEntryPhase(4);

											}
										} else {
											if (enemies[i][j].getX() > 90) {
												enemies[i][j].move(Constants.DIR_NW, 1);

											} else {
												enemies[i][j].setEntryPhase(4);

											}
										}

										break;
									case 4:
										if (enemies[i][j].getY() > 25) {
											enemies[i][j].move(Constants.DIR_N, 1);

										} else {
											enemies[i][j].setEntryPhase(5);
										}

										break;
									case 5:
										enemies[i][j].setDirection(Constants.DIR_N);
									case 6:
										/*
										 * if (enemies[i][j].getX() < enemies[i][j].getFormationX()) {
										 * enemies[i][j].setX(enemies[i][j].getX() + 1); } else {
										 * enemies[i][j].setX(enemies[i][j].getX() - 1); } if (enemies[i][j].getY() <
										 * enemies[i][j].getFormationY()) { enemies[i][j].setY(enemies[i][j].getY() +
										 * 1); } else { enemies[i][j].setY(enemies[i][j].getY() - 1); } break;
										 * 
										 */
										if (enemies[i][j].getX() < enemies[i][j].getFormationX()) {
											enemies[i][j].setX(enemies[i][j].getX() + 1);

										} else if (enemies[i][j].getX() > enemies[i][j].getFormationX()) {

											enemies[i][j].setX(enemies[i][j].getX() - 1);
										}
										if (enemies[i][j].getY() < enemies[i][j].getFormationY()) {
											enemies[i][j].setY(enemies[i][j].getY() + 1);

										} else if (enemies[i][j].getY() > enemies[i][j].getFormationY()) {

											enemies[i][j].setY(enemies[i][j].getY() - 1);
										}
										if (enemies[i][j].getX() == enemies[i][j].getFormationX()
												&& enemies[i][j].getY() == enemies[i][j].getFormationY()) {
											enemies[i][j].setEntryPhase(7);
										}
										break;

									case 7:
										enemies[i][j].setEntryPhase(8);
										break;
									}
								}

								// ~~ Entrada de los Enterprises ~~ \\
								if (enemies[i][j].getType() == 'E') {
									switch (enemies[i][j].getEntryPhase()) {
									case 0:

										if (enemies[i][j].getY() > 215) {
											enemies[i][j].move(Constants.DIR_N, 1);
										} else {
											enemies[i][j].setEntryPhase(1);
										}

										break;
									case 1:
										if (enemies[i][j].getID() % 2 == 0) {

											if (enemies[i][j].getX() < 130) {
												enemies[i][j].move(Constants.DIR_NE, 1);
											} else {
												enemies[i][j].setEntryPhase(2);
											}

										} else {
											if (enemies[i][j].getX() > 40) {
												enemies[i][j].move(Constants.DIR_NW, 1);
											} else {
												enemies[i][j].setEntryPhase(2);
											}
										}
										break;
									case 2:
										/*
										 * Directions: 1 Thru 16 e mod 16 | e % 16 --> r < 16
										 * 
										 * move(DIRECTION, STEPS) move(enemies[i][j].getDirection() + 1 % 16, 1) -
										 * Clockwise Ex: 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
										 * move(enemies[i][j].getDirection() + 15 % 16, 1 - Anticlockwise Spin Ex: 0 15
										 * 14 13 12 11 10 9 8 7 6 5 4 3 2 1
										 */

										if (enemies[i][j].getID() % 2 == 0 && enemies[i][j].getEntryCircle() != 3) {
											enemies[i][j].move((enemies[i][j].getDirection() + 1) % 16, 1);
											if (enemies[i][j].getDirection() == 15) { // Si llega a la direcci�n 15,
																						// cuenta como un looping hecho
												enemies[i][j].setEntryCircle(enemies[i][j].getEntryCircle() + 1);
											}
										} else if (enemies[i][j].getID() % 2 == 0) {
											enemies[i][j].setEntryPhase(3);
											enemies[i][j].setEntryCircle(0);
										}
										if (enemies[i][j].getID() % 2 != 0 && enemies[i][j].getEntryCircle() != 3) {
											enemies[i][j].move((enemies[i][j].getDirection() + 15) % 16, 1);
											if (enemies[i][j].getDirection() == 0) {
												enemies[i][j].setEntryCircle(enemies[i][j].getEntryCircle() + 1);
											}
										} else if (enemies[i][j].getID() % 2 != 0) {
											enemies[i][j].setEntryPhase(3);
											enemies[i][j].setEntryCircle(0);

										}
										break;
									case 3:
										if (enemies[i][j].getY() > 110) {
											enemies[i][j].move(Constants.DIR_N, 1);
										} else {
											enemies[i][j].setEntryPhase(4);
										}

										break;
									case 4:
										if (enemies[i][j].getID() % 2 == 0) {

											if (enemies[i][j].getX() > 90) {
												enemies[i][j].move(Constants.DIR_W, 1);
											} else {
												enemies[i][j].setEntryPhase(5);
											}

										} else {
											if (enemies[i][j].getX() < 70) {
												enemies[i][j].move(Constants.DIR_E, 1);
											} else {
												enemies[i][j].setEntryPhase(5);
											}
										}
										break;
									case 5:
										enemies[i][j].setDirection(Constants.DIR_N);
										enemies[i][j].setEntryPhase(6);

										break;
									case 6:

										if (enemies[i][j].getX() < enemies[i][j].getFormationX()) {
											enemies[i][j].setX(enemies[i][j].getX() + 1);

										} else if (enemies[i][j].getX() > enemies[i][j].getFormationX()) {

											enemies[i][j].setX(enemies[i][j].getX() - 1);
										}
										if (enemies[i][j].getY() < enemies[i][j].getFormationY()) {
											enemies[i][j].setY(enemies[i][j].getY() + 1);

										} else if (enemies[i][j].getY() > enemies[i][j].getFormationY()) {

											enemies[i][j].setY(enemies[i][j].getY() - 1);
										}
										if (enemies[i][j].getX() == enemies[i][j].getFormationX()
												&& enemies[i][j].getY() == enemies[i][j].getFormationY()) {
											enemies[i][j].setEntryPhase(7);
										}
										break;
									case 7:
										enemies[i][j].setEntryPhase(8);

										break;

									}
								}
							}

						}

					}

				}
			}

			// ## DISPAROS, MOVIMIENTO Y ELIMINACI�N ## \\
			// ~~ Disparo alcanza a enemigo ~~ \\
			for (int s = 0; s < pl.shot.length; s++) {
				for (int i = 0; i < enemies.length; i++) {
					for (int j = 0; j < enemies[i].length; j++) {
						if (pl.shot[s] != null) {
							if (enemies[i][j] != null) {
								// La hitbox define el intervalo de posible coincidencia entre las coordendas de los
								// torpedos y las de los enemigos.
								if (((pl.shot[s].getY() >= enemies[i][j].getY() - Constants.HITBOXSIZE)
										&& (pl.shot[s].getY() <= enemies[i][j].getY() + Constants.HITBOXSIZE))
										&& (pl.shot[s].getX() >= enemies[i][j].getX() - Constants.HITBOXSIZE)
										&& (pl.shot[s].getX() <= enemies[i][j].getX() + Constants.HITBOXSIZE)
										&& !pl.shot[s].isOut() && enemies[i][j].getHealth() != 0) {

									pl.setEnemyHit(pl.getEnemyHit() + 1); // Aumentamos los aciertos en 1 unidad
									board.gb_setSpriteVisible(pl.shot[s].getID(), false); // Se hace invisible el disparo
									pl.shot[s].setOut(true); // Se marca como out el disparo
									switch (enemies[i][j].getType()) {
									case 'Z':
										enemies[i][j].decreaseHealth();
										if (enemies[i][j].getHealth() == 0) {
											enemies[i][j].setDeadAnimation(true);

											board.gb_println("The enemy " + enemies[i][j].getID()
													+ " has died. You earn " + enemies[i][j].POINTS + " points.");
											pl.upScore(enemies[i][j].POINTS);
											if (trigger_soundsActive) {
												if (!trigger_enemiesFreezed) {
													new sound.SoundLib("sound/sound_dead2.wav").start();
												} else {
													new sound.SoundLib("sound/sound_deadFreeze.wav").start();
												}
											}

											var_killedEnemies++;
										}
										break;
									case 'G':
										enemies[i][j].decreaseHealth();
										if (enemies[i][j].getHealth() == 0) {
											enemies[i][j].setSprite("explosion20.png");
											enemies[i][j].setDeadAnimation(true);
											board.gb_println("The enemy " + enemies[i][j].getID()
													+ " has died. You earn " + enemies[i][j].POINTS + " points.");
											pl.upScore(enemies[i][j].POINTS);
											if (trigger_soundsActive) {
												if (!trigger_enemiesFreezed) {
													new sound.SoundLib("sound/sound_dead.wav").start();
												} else {
													new sound.SoundLib("sound/sound_deadFreeze.wav").start();
												}
											}
											var_killedEnemies++;
										}
										break;

									case 'C':
										if (enemies[i][j].getHealth() > 1) {
											enemies[i][j].decreaseHealth();
											enemies[i][j].setSprite("enemy9.png");
											if (trigger_soundsActive) {
												if (trigger_enemiesFreezed) {
													new sound.SoundLib("sound/sound_deadFreeze.wav").start();
												} else {
													new sound.SoundLib("sound/sound_galagaInjured.wav").start();

												}

											}

										} else {
											enemies[i][j].setSprite("explosion20.png");
											enemies[i][j].decreaseHealth();
											enemies[i][j].setDeadAnimation(true);
											if (!trigger_bonusInvincibleActive) {
												screenElement.showText("CommanderDefeated", true);
												trigger_enemyDefeatedTxtShowing = true;
											}
											board.gb_println("The enemy " + enemies[i][j].getID()
													+ " has died. You earn " + enemies[i][j].POINTS + " points.");
											pl.upScore(enemies[i][j].POINTS);
											if (trigger_soundsActive) {
												new sound.SoundLib("sound/sound_galagaKilled.wav").start();
											}
											var_killedEnemies++;

										}

										break;
									case 'E':
										if (enemies[i][j].getHealth() == 3) {
											enemies[i][j].decreaseHealth();
											enemies[i][j].setSprite("enemyL2.png");
											if (trigger_soundsActive) {
												if (trigger_enemiesFreezed) {
													new sound.SoundLib("sound/sound_deadFreeze.wav").start();
												} else {
													new sound.SoundLib("sound/sound_enterpriseInjured.wav").start();

												}

											}

										} else if (enemies[i][j].getHealth() == 2) {
											enemies[i][j].decreaseHealth();
											enemies[i][j].setSprite("enemyL3.png");
											if (trigger_soundsActive) {
												if (trigger_enemiesFreezed) {
													new sound.SoundLib("sound/sound_deadFreeze.wav").start();
												} else {
													new sound.SoundLib("sound/sound_enterpriseInjured.wav").start();

												}

											}
										} else {

											enemies[i][j].setSprite("explosion20.png");
											enemies[i][j].decreaseHealth();
											enemies[i][j].setDeadAnimation(true);
											if (!trigger_bonusInvincibleActive) {
												screenElement.showText("EnterpriseDefeated", true);
												trigger_enemyDefeatedTxtShowing = true;
											}
											board.gb_println("The enemy " + enemies[i][j].getID()
													+ " has died. You earn " + enemies[i][j].POINTS + " points.");
											pl.upScore(enemies[i][j].POINTS);
											if (trigger_soundsActive) {
												new sound.SoundLib("sound/sound_enterpriseKilled.wav").start();
											}
											var_killedEnemies++;

										}

										break;
									}

									if (enemies[i][j].isFreezed()) {
										pl.upScore(10); // Si est� congelado, 10 puntos m�s
										board.gb_println("Freezed Enemy! - Extra 10 points");
									}
								}
							}
						}
					}
				}

			}
			// ~~ Disparos del jugador ~~ \\
			for (int i = 0; i < pl.shot.length; i++) {
				if (pl.shot[i] != null) {
					pl.shot[i].shotMoveNorth();

					board.gb_moveSpriteCoord(pl.shot[i].getID(), pl.shot[i].getX(), pl.shot[i].getY());
					if (pl.shot[i].getY() < -10) {
						pl.shot[i].setOut(true);
					}
					if (pl.shot[i].isOut()) {
						board.gb_setSpriteVisible(pl.shot[i].getID(), false);
						pl.shot[i] = null;
					}
				}

			}

			// ~~ Disparos de los enemigos ~~ \\
			for (int i = 0; i < enemies.length; i++) {
				for (int j = 0; j < enemies[i].length; j++) {
					if (enemies[i][j] != null) {
						for (int s = 0; s < enemies[i][j].shot.length; s++) {
							if (enemies[i][j].shot[s] != null) {
								enemies[i][j].shot[s].shotMoveSouth();
								board.gb_moveSpriteCoord(enemies[i][j].shot[s].getID(), enemies[i][j].shot[s].getX(),
										enemies[i][j].shot[s].getY());
								if (enemies[i][j].shot[s].getY() > 220) {
									enemies[i][j].shot[s].setOut(true);
								}
								if (enemies[i][j].shot[s].isOut()) {
									board.gb_setSpriteVisible(enemies[i][j].shot[s].getID(), false);
									enemies[i][j].shot[s] = null;
									enemies[i][j].shotCount--;

								}
							}
						}

					}
				}
			}

			// ## MUERTE DE TODOS LOS ENEMIGOS ## \\
			if (var_killedEnemies == var_zakoCounter + var_goeiCounter + var_commanderCounter + var_enterpriseCounter
					&& trigger_levelStart | trigger_allEnemyDead && trigger_levelStart) {
				if (var_counter[2] < 15) { // Espera temporal antes de finalizar el nivel para poder ver las

					var_counter[2]++;
				} else {
					for (int i = 0; i < pl.shot.length; i++) { // Elimina los disparos restantes del mapa
						if (pl.shot[i] != null) {
							board.gb_setSpriteVisible(pl.shot[i].getID(), false);

							pl.shot[i] = null; // animaciones de muerte del ultimo enemigo
						}
					}
					var_counter[2] = 0;
					board.gb_clearConsole();
					board.gb_clearCommandBar();
					if (trigger_soundsActive) {
						new sound.SoundLib("sound/sound_levelCompleted.wav").start();
					}
					trigger_levelCompleted = true;
				}
			}
			// ## C�DIGO DE EJECUCI�N POR NIVEL ## \\
			// ################################### \\
			if (trigger_welcomeShowed) { // Se ejecuta una vez se haya mostrado el logo de inicio
				switch (var_actualLevel) {
				case 0: // Inicio / Reinicio del juego

					pl.resetAttributes(); // Reinicia todos los atributos del jugador a 0
					// Eliminaci�n de los enemigos
					for (int i = 0; i < enemies.length; i++) {
						for (int j = 0; j < enemies[i].length; j++) {
							if (enemies[i][j] != null) {
								board.gb_setSpriteVisible(enemies[i][j].getID(), false);

								enemies[i][j] = null;
							}
						}
					}
					trigger_enemiesReachedFormation = false;
					trigger_formationAnimated = false;
					trigger_enemiesFreezed = false;
					trigger_levelGenerated = false;
					trigger_bonusInvincibleActive = false;
					trigger_bonusActive = false;
					trigger_allEnemyDead = false;
					var_counter[18] = 0; // Contador que controla el tiempo antes de finalizar el nivel
					trigger_levelStart = false;
					var_shotCounter = 0; // Contador de disparos que se utiliza para recorrer el array de disparos
					var_zakoCounter = 0; // Contador de Zakos
					var_goeiCounter = 0; // Contador de Goeis
					var_commanderCounter = 0; // Contador de Comandantes G�laga
					var_killedEnemies = 0; // Contador de enemigos eliminados
					var_bonusCounter = 0;
					var_actualLevel = 1;

					break;
				case 1:
					if (!trigger_levelGenerated) {
						// ## GENERACI�N DEL NIVEL 1 ## //
						// Posiciones iniciales de los enemigos antes de los movimientos de entrada
						// [0] Posici�n en el array define la coordenada X
						// [1] Posici�n en el array define la coordenada Y
						// {X inicial, Y inicial}
						int GoeiInit[] = new int[] { 85, 230 };
						int ZakoInit[] = new int[] { 85, 110 };
						int CommanderInit[] = new int[] { 85, -10 };
						int EnterpriseInit[] = new int[] { 85, 230 };

						// ## CREACI�N DE LOS ENEMIGOS ## //
						// Obtenci�n de la posici�n y la cantidad de enemigos de la matriz de nivel //
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								switch (Constants.LV1FORMATION[i][j]) {
								case 'Z':
									// Distribuci�n homog�nea de los Zakos (en grupos seg�n la paridad del n�mero
									// del Zako)
									if (var_zakoCounter % 2 == 0) {
										enemies[i][j] = new EnemyZako(100 + var_zakoCounter,
												ZakoInit[0] - 100 - var_zakoCounter * 5, ZakoInit[1], Constants.DIR_N,

												"enemy3.png"); // Restamos a los enemigos las casillas necesarias
																// para
																// alcanzar la
																// posicion
																// inicial
										var_zakoCounter++;
									} else {
										enemies[i][j] = new EnemyZako(100 + var_zakoCounter,
												ZakoInit[0] + 100 + var_zakoCounter * 5 - 1 * 5, ZakoInit[1],
												Constants.DIR_N, "enemy3.png"); // Restamos 1*5(coordenadas que nos
																				// movemos) en el
																				// caso de
																				// los impares para
																				// compensar la posici�n y que est�n
																				// a
																				// las mismas
																				// casillas
																				// de distancia del
																				// jugador
										var_zakoCounter++;
									}
									break;
								case 'G':
									if (var_goeiCounter % 2 == 0) { // Divisi�n en dos grupos de posiciones para los
																	// Goeis
										enemies[i][j] = new EnemyGoei(200 + var_goeiCounter, GoeiInit[0] - 70,
												GoeiInit[1] + (var_goeiCounter * 5), Constants.DIR_N, "enemy2.png");
										// Le restamos a X 70, para establecer el grupo 1 a la izquierda 7 casillas
										// GoeiInit[1] + (i * 5) establece la separaci�n entre enemigos en el eje Y.
										var_goeiCounter++;
									} else {
										enemies[i][j] = new EnemyGoei(200 + var_goeiCounter, GoeiInit[0] + 70,
												GoeiInit[1] + (var_goeiCounter * 5 - 5), Constants.DIR_N, "enemy2.png");
										// Le sumamos a X 70 para establecer el grupo 2 7 casillas a la derecha.
										// GoeiInit[1] + (i * 5 - 5) establece la separaci�n entre enemigos en el
										// eje Y.
										// Corrige la posici�n restando 5 para estar alineado con respecto a los
										// pares
										var_goeiCounter++;
									}
									break;
								case 'C':
									enemies[i][j] = new EnemyCommander(300 + var_commanderCounter, CommanderInit[0],
											CommanderInit[1] - var_commanderCounter * 5, Constants.DIR_N, "enemy1.png");
									var_commanderCounter++;
									break;
								case 'E':
									enemies[i][j] = new EnemyEnterprise(400 + var_enterpriseCounter, EnterpriseInit[0],
											EnterpriseInit[1] - var_enterpriseCounter * 5, Constants.DIR_N,
											"enemyL.png");
									var_enterpriseCounter++;
									break;
								case '.':
									enemies[i][j] = null;
									break;
								}
								// OBTENCI�N DE LAS COORDENADAS DE LA FORMACI�N
								if (enemies[i][j] != null) {
									enemies[i][j].setFormationX(40 + (j * 10)); // La posici�n en coordenadas del
																				// array
																				// comienza en X
																				// = 40 [Mirar esquema Memoria]
									enemies[i][j].setFormationY(10 + (i * 10)); // La posici�n en coordenadas del
																				// array
																				// comienza en Y
																				// = 10 [Mirar esquema Memoria]

								}

							}

						}

						// Adici�n de los enemigos
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								if (enemies[i][j] != null) {
									board.gb_addSprite(enemies[i][j].getID(), enemies[i][j].getSprite(), true);
									board.gb_moveSpriteCoord(enemies[i][j].getID(), enemies[i][j].getX(),
											enemies[i][j].getY());
									board.gb_setSpriteImage(enemies[i][j].getID(), enemies[i][j].getSprite());
									board.gb_setSpriteVisible(enemies[i][j].getID(), true);
									enemies[i][j].setRandomPath((int) (Math.random() * 6)); // Generaci�n de un camino
																							// aleatorio que seguir� el
																							// enemigo inicialmente
															 								// hasta que se genere otro

								}
							}
						}
						screenElement.addAllImageSprites(); // Vuelve a a�adir las im�genes en el tablero para que
															// queden por encima de los enemigos
						screenElement.getDigits(pl.getScore());

						trigger_levelGenerated = true;

					} else {
						// ## PREVIO AL COMIENZO DEL NIVEL ##
						if (var_counter[0] < 30 && !trigger_levelStart) {
							screenElement.showText("Level" + var_actualLevel, true);
							var_counter[0]++;

						} else {
							if (var_counter[0] == 30 && !trigger_levelStart) { // Tras el
																				// tiempo
								// marcado por
								// el contador
								board.gb_println("~ GALAGA ~ Level 1"); // Muestra en la consola el nivel
								screenElement.hideText(); // Oculta el texto de nivel 1
								if (trigger_soundsActive) {
									new sound.SoundLib("sound/sound_levelStart.wav").start();
								}
								trigger_levelStart = true;
								var_counter[0] = 0;
							}
						}

					}

					// ## COMIENZO DEL NIVEL ## //
					if (trigger_levelStart) { // Si se ha activado trigger_levelStart, comienza el nivel

						if (trigger_enemyDefeatedTxtShowing) {
							if (var_counter[4] < 40) {
								var_counter[4]++;
							} else {
								var_counter[4] = 0;
								screenElement.hideText(); // Oculta el texto de Comandante derrotado
								trigger_enemyDefeatedTxtShowing = false;
							}
						}
					}

					break;
				case 2:
					if (!trigger_levelGenerated) {
						// ## GENERACI�N DEL NIVEL 1 ## //
						// Posiciones iniciales de los enemigos antes de los movimientos de entrada
						// [0] Posici�n en el array define la coordenada X
						// [1] Posici�n en el array define la coordenada Y
						// {X inicial, Y inicial}
						int GoeiInit[] = new int[] { 85, 230 };
						int ZakoInit[] = new int[] { 85, 110 };
						int CommanderInit[] = new int[] { 85, -10 };
						int EnterpriseInit[] = new int[] { 85, 230 };

						// ## CREACI�N DE LOS ENEMIGOS ## //
						// Obtenci�n de la posici�n y la cantidad de enemigos de la matriz de nivel //
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								switch (Constants.LV2FORMATION[i][j]) {
								case 'Z':
									// Distribuci�n homog�nea de los Zakos (en grupos seg�n la paridad del n�mero
									// del Zako)
									if (var_zakoCounter % 2 == 0) {
										enemies[i][j] = new EnemyZako(100 + var_zakoCounter,
												ZakoInit[0] - 100 - var_zakoCounter * 5, ZakoInit[1], Constants.DIR_N,

												"enemy3.png"); // Restamos a los enemigos las casillas necesarias
																// para
																// alcanzar la
																// posicion
																// inicial
										var_zakoCounter++;
									} else {
										enemies[i][j] = new EnemyZako(100 + var_zakoCounter,
												ZakoInit[0] + 100 + var_zakoCounter * 5 - 1 * 5, ZakoInit[1],
												Constants.DIR_N, "enemy3.png"); // Restamos 1*5(coordenadas que nos
																				// movemos) en el
																				// caso de
																				// los impares para
																				// compensar la posici�n y que est�n
																				// a
																				// las mismas
																				// casillas
																				// de distancia del
																				// jugador
										var_zakoCounter++;
									}
									break;
								case 'G':
									if (var_goeiCounter % 2 == 0) { // Divisi�n en dos grupos de posiciones para los
																	// Goeis
										enemies[i][j] = new EnemyGoei(200 + var_goeiCounter, GoeiInit[0] - 70,
												GoeiInit[1] + (var_goeiCounter * 5), Constants.DIR_N, "enemy2.png");
										// Le restamos a X 70, para establecer el grupo 1 a la izquierda 7 casillas
										// GoeiInit[1] + (i * 5) establece la separaci�n entre enemigos en el eje Y.
										var_goeiCounter++;
									} else {
										enemies[i][j] = new EnemyGoei(200 + var_goeiCounter, GoeiInit[0] + 70,
												GoeiInit[1] + (var_goeiCounter * 5 - 5), Constants.DIR_N, "enemy2.png");
										// Le sumamos a X 70 para establecer el grupo 2 7 casillas a la derecha.
										// GoeiInit[1] + (i * 5 - 5) establece la separaci�n entre enemigos en el
										// eje Y.
										// Corrige la posici�n restando 5 para estar alineado con respecto a los
										// pares
										var_goeiCounter++;
									}
									break;
								case 'C':
									enemies[i][j] = new EnemyCommander(300 + var_commanderCounter, CommanderInit[0],
											CommanderInit[1] - var_commanderCounter * 5, Constants.DIR_N, "enemy1.png");
									var_commanderCounter++;
									break;
								case 'E':
									enemies[i][j] = new EnemyEnterprise(400 + var_enterpriseCounter, EnterpriseInit[0],
											EnterpriseInit[1] - var_enterpriseCounter * 5, Constants.DIR_N,
											"enemyL.png");
									var_enterpriseCounter++;
									break;
								case '.':
									enemies[i][j] = null;
									break;
								}
								// OBTENCI�N DE LAS COORDENADAS DE LA FORMACI�N
								if (enemies[i][j] != null) {
									enemies[i][j].setFormationX(40 + (j * 10)); // La posici�n en coordenadas del
																				// array
																				// comienza en X
																				// = 40 [Mirar esquema Memoria]
									enemies[i][j].setFormationY(10 + (i * 10)); // La posici�n en coordenadas del
																				// array
																				// comienza en Y
																				// = 10 [Mirar esquema Memoria]

								}

							}
						}

						// Adici�n de los enemigos
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								if (enemies[i][j] != null) {
									board.gb_addSprite(enemies[i][j].getID(), enemies[i][j].getSprite(), true);
									board.gb_moveSpriteCoord(enemies[i][j].getID(), enemies[i][j].getX(),
											enemies[i][j].getY());
									board.gb_setSpriteImage(enemies[i][j].getID(), enemies[i][j].getSprite());
									board.gb_setSpriteVisible(enemies[i][j].getID(), true);
									enemies[i][j].setRandomPath((int) (Math.random() * 6)); // Generaci�n de un camino
																							// aleatorio que seguir� el
																							// enemigo inicialmente
																							// hasta que se genere otro

								}
							}
						}
						screenElement.addAllImageSprites(); // Vuelve a a�adir las im�genes en el tablero para que
															// queden por encima de los enemigos
						screenElement.getDigits(pl.getScore());
						trigger_levelGenerated = true;

					} else {
						// ## PREVIO AL COMIENZO DEL NIVEL ##
						if (var_counter[0] < 30 && !trigger_levelStart) {
							screenElement.showText("Level" + var_actualLevel, true);
							var_counter[0]++;

						} else {
							if (var_counter[0] == 30 && !trigger_levelStart) { // Tras el
																				// tiempo
								// marcado por
								// el contador
								board.gb_println("~ GALAGA ~ Level 2"); // Muestra en la consola el nivel
								screenElement.hideText(); // Oculta el texto de nivel 1
								if (trigger_soundsActive) {
									new sound.SoundLib("sound/sound_levelStart.wav").start();
								}
								trigger_levelStart = true;
								var_counter[0] = 0;
							}
						}

					}

					// ## COMIENZO DEL NIVEL ## //
					if (trigger_levelStart) { // Si se ha activado trigger_levelStart, comienza el nivel

						if (trigger_enemyDefeatedTxtShowing) {
							if (var_counter[4] < 40) {
								var_counter[4]++;
							} else {
								var_counter[4] = 0;
								screenElement.hideText(); // Oculta el texto de Comandante derrotado
								trigger_enemyDefeatedTxtShowing = false;
							}
						}
					}
					break;
				case 3:
					if (!trigger_levelGenerated) {
						// ## GENERACI�N DEL NIVEL 1 ## //
						// Posiciones iniciales de los enemigos antes de los movimientos de entrada
						// [0] Posici�n en el array define la coordenada X
						// [1] Posici�n en el array define la coordenada Y
						// {X inicial, Y inicial}
						int GoeiInit[] = new int[] { 85, 230 };
						int ZakoInit[] = new int[] { 85, 110 };
						int CommanderInit[] = new int[] { 85, -10 };
						int EnterpriseInit[] = new int[] { 85, 230 };

						// ## CREACI�N DE LOS ENEMIGOS ## //
						// Obtenci�n de la posici�n y la cantidad de enemigos de la matriz de nivel //
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								switch (Constants.LV3FORMATION[i][j]) {
								case 'Z':
									// Distribuci�n homog�nea de los Zakos (en grupos seg�n la paridad del n�mero
									// del Zako)
									if (var_zakoCounter % 2 == 0) {
										enemies[i][j] = new EnemyZako(100 + var_zakoCounter,
												ZakoInit[0] - 100 - var_zakoCounter * 5, ZakoInit[1], Constants.DIR_N,

												"enemy3.png"); // Restamos a los enemigos las casillas necesarias
																// para
																// alcanzar la
																// posicion
																// inicial
										var_zakoCounter++;
									} else {
										enemies[i][j] = new EnemyZako(100 + var_zakoCounter,
												ZakoInit[0] + 100 + var_zakoCounter * 5 - 1 * 5, ZakoInit[1],
												Constants.DIR_N, "enemy3.png"); // Restamos 1*5(coordenadas que nos
																				// movemos) en el
																				// caso de
																				// los impares para
																				// compensar la posici�n y que est�n
																				// a
																				// las mismas
																				// casillas
																				// de distancia del
																				// jugador
										var_zakoCounter++;
									}
									break;
								case 'G':
									if (var_goeiCounter % 2 == 0) { // Divisi�n en dos grupos de posiciones para los
																	// Goeis
										enemies[i][j] = new EnemyGoei(200 + var_goeiCounter, GoeiInit[0] - 70,
												GoeiInit[1] + (var_goeiCounter * 5), Constants.DIR_N, "enemy2.png");
										// Le restamos a X 70, para establecer el grupo 1 a la izquierda 7 casillas
										// GoeiInit[1] + (i * 5) establece la separaci�n entre enemigos en el eje Y.
										var_goeiCounter++;
									} else {
										enemies[i][j] = new EnemyGoei(200 + var_goeiCounter, GoeiInit[0] + 70,
												GoeiInit[1] + (var_goeiCounter * 5 - 5), Constants.DIR_N, "enemy2.png");
										// Le sumamos a X 70 para establecer el grupo 2 7 casillas a la derecha.
										// GoeiInit[1] + (i * 5 - 5) establece la separaci�n entre enemigos en el
										// eje Y.
										// Corrige la posici�n restando 5 para estar alineado con respecto a los
										// pares
										var_goeiCounter++;
									}
									break;
								case 'C':
									enemies[i][j] = new EnemyCommander(300 + var_commanderCounter, CommanderInit[0],
											CommanderInit[1] - var_commanderCounter * 5, Constants.DIR_N, "enemy1.png");
									var_commanderCounter++;
									break;
								case 'E':
									enemies[i][j] = new EnemyEnterprise(400 + var_enterpriseCounter, EnterpriseInit[0],
											EnterpriseInit[1] - var_enterpriseCounter * 5, Constants.DIR_N,
											"enemyL.png");
									var_enterpriseCounter++;
									break;
								case '.':
									enemies[i][j] = null;
									break;
								}
								// OBTENCI�N DE LAS COORDENADAS DE LA FORMACI�N
								if (enemies[i][j] != null) {
									enemies[i][j].setFormationX(40 + (j * 10)); // La posici�n en coordenadas del
																				// array
																				// comienza en X
																				// = 40 [Mirar esquema Memoria]
									enemies[i][j].setFormationY(10 + (i * 10)); // La posici�n en coordenadas del
																				// array
																				// comienza en Y
																				// = 10 [Mirar esquema Memoria]

								}

							}
						}

						// Adici�n de los enemigos
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								if (enemies[i][j] != null) {
									board.gb_addSprite(enemies[i][j].getID(), enemies[i][j].getSprite(), true);
									board.gb_moveSpriteCoord(enemies[i][j].getID(), enemies[i][j].getX(),
											enemies[i][j].getY());
									board.gb_setSpriteImage(enemies[i][j].getID(), enemies[i][j].getSprite());
									board.gb_setSpriteVisible(enemies[i][j].getID(), true);
									enemies[i][j].setRandomPath((int) (Math.random() * 6)); // Generaci�n de un camino
																							// aleatorio que seguir� el
																							// enemigo inicialmente
																							// hasta que se genere otro

								}
							}
						}
						screenElement.addAllImageSprites(); // Vuelve a a�adir las im�genes en el tablero para que
						// queden por encima de los enemigos
						screenElement.getDigits(pl.getScore());
						trigger_levelGenerated = true;
					} else {
						// ## PREVIO AL COMIENZO DEL NIVEL ##
						if (var_counter[0] < 30 && !trigger_levelStart) {
							screenElement.showText("Level" + var_actualLevel, true);
							var_counter[0]++;

						} else {
							if (var_counter[0] == 30 && !trigger_levelStart) { // Tras el
																				// tiempo
								// marcado por
								// el contador
								board.gb_println("~ GALAGA ~ Level 3"); // Muestra en la consola el nivel
								screenElement.hideText(); // Oculta el texto de nivel 1
								if (trigger_soundsActive) {
									new sound.SoundLib("sound/sound_levelStart.wav").start();
								}
								trigger_levelStart = true;
								var_counter[0] = 0;
							}
						}

					}

					// ## COMIENZO DEL NIVEL ## //
					if (trigger_levelStart) { // Si se ha activado trigger_levelStart, comienza el nivel

						if (trigger_enemyDefeatedTxtShowing) {
							if (var_counter[4] < 40) {
								var_counter[4]++;
							} else {
								var_counter[4] = 0;
								screenElement.hideText(); // Oculta el texto de Comandante derrotado
								trigger_enemyDefeatedTxtShowing = false;
							}
						}
					}
					break;

				case 4:
					if (!trigger_levelGenerated) {
						// ## GENERACI�N DEL NIVEL 1 ## //
						// Posiciones iniciales de los enemigos antes de los movimientos de entrada
						// [0] Posici�n en el array define la coordenada X
						// [1] Posici�n en el array define la coordenada Y
						// {X inicial, Y inicial}
						int GoeiInit[] = new int[] { 85, 230 };
						int ZakoInit[] = new int[] { 85, 110 };
						int CommanderInit[] = new int[] { 85, -10 };
						int EnterpriseInit[] = new int[] { 85, 230 };

						// ## CREACI�N DE LOS ENEMIGOS ## //
						// Obtenci�n de la posici�n y la cantidad de enemigos de la matriz de nivel //
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								switch (Constants.LV4FORMATION[i][j]) {
								case 'Z':
									// Distribuci�n homog�nea de los Zakos (en grupos seg�n la paridad del n�mero
									// del Zako)
									if (var_zakoCounter % 2 == 0) {
										enemies[i][j] = new EnemyZako(100 + var_zakoCounter,
												ZakoInit[0] - 100 - var_zakoCounter * 5, ZakoInit[1], Constants.DIR_N,

												"enemy3.png"); // Restamos a los enemigos las casillas necesarias
																// para
																// alcanzar la
																// posicion
																// inicial
										var_zakoCounter++;
									} else {
										enemies[i][j] = new EnemyZako(100 + var_zakoCounter,
												ZakoInit[0] + 100 + var_zakoCounter * 5 - 1 * 5, ZakoInit[1],
												Constants.DIR_N, "enemy3.png"); // Restamos 1*5(coordenadas que nos
																				// movemos) en el
																				// caso de
																				// los impares para
																				// compensar la posici�n y que est�n
																				// a
																				// las mismas
																				// casillas
																				// de distancia del
																				// jugador
										var_zakoCounter++;
									}
									break;
								case 'G':
									if (var_goeiCounter % 2 == 0) { // Divisi�n en dos grupos de posiciones para los
																	// Goeis
										enemies[i][j] = new EnemyGoei(200 + var_goeiCounter, GoeiInit[0] - 70,
												GoeiInit[1] + (var_goeiCounter * 5), Constants.DIR_N, "enemy2.png");
										// Le restamos a X 70, para establecer el grupo 1 a la izquierda 7 casillas
										// GoeiInit[1] + (i * 5) establece la separaci�n entre enemigos en el eje Y.
										var_goeiCounter++;
									} else {
										enemies[i][j] = new EnemyGoei(200 + var_goeiCounter, GoeiInit[0] + 70,
												GoeiInit[1] + (var_goeiCounter * 5 - 5), Constants.DIR_N, "enemy2.png");
										// Le sumamos a X 70 para establecer el grupo 2 7 casillas a la derecha.
										// GoeiInit[1] + (i * 5 - 5) establece la separaci�n entre enemigos en el
										// eje Y.
										// Corrige la posici�n restando 5 para estar alineado con respecto a los
										// pares
										var_goeiCounter++;
									}
									break;
								case 'C':
									enemies[i][j] = new EnemyCommander(300 + var_commanderCounter, CommanderInit[0],
											CommanderInit[1] - var_commanderCounter * 5, Constants.DIR_N, "enemy1.png");
									var_commanderCounter++;
									break;
								case 'E':
									// Distribuci�n homog�nea de los Enterprises (en grupos seg�n la paridad del
									// n�mero
									// del Enterprise)
									if (var_enterpriseCounter % 2 == 0) {
										enemies[i][j] = new EnemyEnterprise(400 + var_enterpriseCounter,
												EnterpriseInit[0] - 100 - var_enterpriseCounter * 5, EnterpriseInit[1],
												Constants.DIR_N,

												"enemyL.png"); // Restamos a los enemigos las casillas necesarias
																// para
																// alcanzar la
																// posicion
																// inicial
										var_enterpriseCounter++;
									} else {
										enemies[i][j] = new EnemyEnterprise(400 + var_enterpriseCounter,
												EnterpriseInit[0] + 100 + var_enterpriseCounter * 5 - 1 * 5,
												EnterpriseInit[1], Constants.DIR_N, "enemyL.png"); // Restamos
																									// 1*5(coordenadas
																									// que nos
																									// movemos) en el
																									// caso de
																									// los impares para
																									// compensar la
																									// posici�n y que
																									// est�n
																									// a
																									// las mismas
																									// casillas
																									// de distancia del
																									// jugador
										var_enterpriseCounter++;
									}
									break;
								case '.':
									enemies[i][j] = null;
									break;
								}

								// OBTENCI�N DE LAS COORDENADAS DE LA FORMACI�N
								if (enemies[i][j] != null) {
									enemies[i][j].setFormationX(40 + (j * 10)); // La posici�n en coordenadas del
																				// array
																				// comienza en X
																				// = 40 [Mirar esquema Memoria]
									enemies[i][j].setFormationY(10 + (i * 10)); // La posici�n en coordenadas del
																				// array
																				// comienza en Y
																				// = 10 [Mirar esquema Memoria]

								}

							}
						}

						// Adici�n de los enemigos
						for (int i = 0; i < enemies.length; i++) {
							for (int j = 0; j < enemies[i].length; j++) {
								if (enemies[i][j] != null) {
									board.gb_addSprite(enemies[i][j].getID(), enemies[i][j].getSprite(), true);
									board.gb_moveSpriteCoord(enemies[i][j].getID(), enemies[i][j].getX(),
											enemies[i][j].getY());
									board.gb_setSpriteImage(enemies[i][j].getID(), enemies[i][j].getSprite());
									board.gb_setSpriteVisible(enemies[i][j].getID(), true);
									enemies[i][j].setRandomPath((int) (Math.random() * 6)); // Generaci�n de un camino
																							// aleatorio que seguir� el
																							// enemigo inicialmente
																							// hasta que se genere otro

								}
							}
						}
						screenElement.addAllImageSprites(); // Vuelve a a�adir las im�genes en el tablero para que
						// queden por encima de los enemigos
						screenElement.getDigits(pl.getScore());

						trigger_levelGenerated = true;

					} else {
						// ## PREVIO AL COMIENZO DEL NIVEL ##
						if (var_counter[0] < 30 && !trigger_levelStart) {
							screenElement.showText("Level" + var_actualLevel, true);
							var_counter[0]++;

						} else {
							if (var_counter[0] == 30 && !trigger_levelStart) { // Tras el
																				// tiempo
								// marcado por
								// el contador
								board.gb_println("~ GALAGA ~ FINAL LEVEL"); // Muestra en la consola el nivel
								screenElement.hideText(); // Oculta el texto de nivel 1
								if (trigger_soundsActive) {
									new sound.SoundLib("sound/sound_levelStart.wav").start();
								}
								trigger_levelStart = true;
								var_counter[0] = 0;
							}
						}

					}

					// ## COMIENZO DEL NIVEL ## //
					if (trigger_levelStart) { // Si se ha activado trigger_levelStart, comienza el nivel

						if (trigger_enemyDefeatedTxtShowing) {
							if (var_counter[4] < 40) {
								var_counter[4]++;
							} else {
								var_counter[4] = 0;
								screenElement.hideText(); // Oculta el texto de Comandante derrotado
								trigger_enemyDefeatedTxtShowing = false;
							}
						}

					}
					break;

				}
			}

			switch (pl.getSpeed())

			{
			case 3:
				Thread.sleep(33L);
				break;
			case 6:
				Thread.sleep(27L);
				break;
			case 9:
				Thread.sleep(15L);
				break;
			}

		} while (trigger_gameRunning);

		screenElement.addAllImageSprites(); // A�ade de nuevo todos los sprites para que 
											// los disparos queden por debajo
		board.gb_setSpriteVisible(604, true); // Hacer visible la pantalla final
		if (pl.getHealth() <= 0)

		{
			board.gb_setSpriteImage(604, "img_gameOver0.png", 487, 640);
		}
		screenElement.moveScoreBoardEnd(); // Mover el Scoreboard
											// a la posici�n que
											// le corresponde en
											// la pantalla
		// final
		screenElement.getDigits(0); // Obtener todos los d�gitos a 0
		screenElement.addEndLives(); // A�adir el indicador de vidas del final
		screenElement.hideText();
		// ## LOOhP PANTALLA FINAL ## \\
		do {
			if (var_counter[19] < 5) {
				var_counter[19]++;
			} else {
				if (!screenElement.getDigitsAnimation(pl.getScore())) {
					screenElement.getDigitsAnimation(pl.getScore()); // Obtener
																		// la
																		// animaci�n
																		// de
																		// los
																		// d�gitos
																		// para
																		// la
																		// puntuaci�n
																		// del
																		// jugador
					if (pl.getScore() != 0) {
						new sound.SoundLib("sound/sound_numberUp.wav").start(); // Estos sonidos suenan
																				// independientemente de si est�
																				// activado el sonido del juego o no
					}
					var_counter[19] = 0;

				} else {
					var_counter[19] = 6;
				}
			}

			if (var_counter[19] == 6) {
				if (var_counter[20] < 15) {
					var_counter[20]++;
				} else if (var_counter[21] != 3) {

					screenElement.getLivesAnimation(pl.getHealth()); // Obtener la animaci�n de las vidas finales
					var_counter[20] = 0;
					var_counter[21]++;
				}
			}

			if (var_counter[21] == 3) {
				if (var_counter[22] < 15) {
					var_counter[22]++;
				} else if (var_counter[22] == 15) {
					if (pl.getScore() >= Constants.MEDALPOINTS[0]) {
						if (pl.getHealth() > 0) {
							board.gb_setSpriteImage(604, "img_victory1.png", 487, 640);
							trigger_endFireworksActive = true;
							new sound.SoundLib("sound/sound_fireworks.wav").start();
						} else {
							board.gb_setSpriteImage(604, "img_gameOver1.png", 487, 640);
						}

						new sound.SoundLib("sound/sound_medal1.wav").start(); // Estos sonidos suenan
																				// independientemente de si est�
																				// activado el sonido del juego
																				// o no
						var_counter[22] = 26;
					}
				}
				if (var_counter[22] >= 26 && var_counter[22] < 71) {
					var_counter[22]++;
				} else if (var_counter[22] == 71) {
					if (pl.getScore() >= Constants.MEDALPOINTS[1]) {

						if (pl.getHealth() > 0) {
							board.gb_setSpriteImage(604, "img_victory2.png", 487, 640);
						} else {
							board.gb_setSpriteImage(604, "img_gameOver2.png", 487, 640);
						}
						new sound.SoundLib("sound/sound_medal2.wav").start(); // Estos
																				// sonidos
																				// suenan
						// independientemente de si est�
						// activado el sonido del juego o no
						var_counter[22] = 72;
					}

				}
				if (var_counter[22] >= 72 && var_counter[22] < 120) {
					var_counter[22]++;
				} else if (var_counter[22] == 120) {
					if (pl.getScore() >= Constants.MEDALPOINTS[2]) {
						if (pl.getHealth() != 0) {
							board.gb_setSpriteImage(604, "img_victory3.png", 487, 640);
						} else {
							board.gb_setSpriteImage(604, "img_gameOver3.png", 487, 640);
						}
						new sound.SoundLib("sound/sound_medal3.wav").start(); // Estos
																				// sonidos
																				// suenan
						// independientemente de si est�
						// activado el sonido del juego o no
						var_counter[22] = 121;

					}

				}
			}

			if (pl.getHealth() > 0 && trigger_endFireworksActive) {
				if (var_counter[23] < 5) {

					var_counter[23]++;
				} else {
					var_counter[23] = 0;
					screenElement.getFireworkEndEffect(0);

				}
				if (var_counter[24] < 4) {
					var_counter[24]++;
				} else {
					screenElement.getFireworkEndEffect(1);
					var_counter[24] = 0;
				}
				if (var_counter[25] < 6) {
					var_counter[25]++;
				} else {
					screenElement.getFireworkEndEffect(2);
					var_counter[25] = 0;
				}
			}
			String lastAction = board.gb_getLastAction().trim();

			if (lastAction.equals("tab")) {
				board.gb_showMessageDialog(
						"\t - GAME FINISHED - \n \n \t Pablo D�az-Heredero Garc�a \n \n \t \n [uc3m - 2018]");
				System.exit(0);
			}
			if (lastAction.contains("new game")) {
				board.gb_showMessageDialog("You can't start a new game at the End Screen. Please, restart game");
			}

			if (lastAction.equals("exit game")) {
				board.gb_showMessageDialog(
						"\t - GAME FINISHED - \n \n \t Pablo D�az-Heredero Garc�a \n \n \t \n [uc3m - 2018]");
				System.exit(0);
			}
			Thread.sleep(30L);
		} while (!trigger_gameRunning);

	}

}
