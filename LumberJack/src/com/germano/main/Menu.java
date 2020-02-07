package com.germano.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.germano.world.Camera;
import com.germano.world.World;

public class Menu {

	/************ Atributos ************/

	// Controle da tela de menu
	public boolean isMenu = true;
	public String[] options = { "Novo Jogo", "Carregar Jogo", "Sair" };
	public int currentOption = 0;
	public int maxOptionsMenu = options.length - 1;

	// Controle da tela de dificuldade
	public boolean isDific = false;
	public String[] difficult = { "EASY", "MEDIUM", "HARD" };
	public int currentDific = 0;
	public int maxDific = difficult.length - 1;

	public boolean up;
	public boolean down;
	public boolean enter;
	public boolean sair;

	// Verifica se o jogo foi pausado no meio
	public static boolean pause = false;
	public boolean notStarted = false;

	// Gerencia de save
	public static boolean saveExists = false;
	public static boolean saveGame = false;

	/************ L�gica ************/

	public void tick() {

		File file = new File("save.txt");
		if (file.exists()) {
			saveExists = true;
		} else {
			saveExists = false;
		}

		if (up) {
			up = false;
			if (isMenu) {
				currentOption--;
				if (currentOption < 0) {
					currentOption = maxOptionsMenu;
				}
			} else if (isDific) {
				currentDific--;
				if (currentDific < 0) {
					currentDific = maxDific;
				}
			}
		}

		if (down) {
			down = false;
			if (isMenu) {
				currentOption++;
				if (currentOption > maxOptionsMenu) {
					currentOption = 0;
				}
			} else if (isDific) {
				currentDific++;
				if (currentDific > maxDific) {
					currentDific = 0;
				}
			}
		}

		if (enter) {
			enter = false;
			if (isMenu && options[currentOption].equals("Novo Jogo")) { // Se escolher Novo Jogo
				if (pause) {// Se vier ao menu apertando esc
					isDific = false;
					Game.gameState = "NORMAL";
					pause = false;
				} else {// Se vier do menu principal
					isDific = true;
					isMenu = false;
					file = new File("save.txt");
					file.delete();
				}
			} else if (isDific && difficult[currentDific].contentEquals("EASY")) {
				Game.dificult = "EASY";
				Game.gameState = "NORMAL";
				isDific = false;
				isMenu = false;
				pause = false;
			} else if (isDific && difficult[currentDific].contentEquals("MEDIUM")) {
				Game.dificult = "MEDIUM";
				Game.gameState = "NORMAL";
				isDific = false;
				isMenu = false;
				pause = false;
			} else if (difficult[currentDific].contentEquals("HARD")) {
				Game.dificult = "HARD";
				Game.gameState = "NORMAL";
				isDific = false;
				isMenu = false;
				pause = false;
			} else if (options[currentOption].equals("Carregar Jogo")) {
				file = new File("save.txt");
				if (file.exists()) {
					String saver = loadGame(10);
					applySave(saver);
				}
			}
			if (options[currentOption].equals("Sair")) {
				System.exit(1);
			}
		}
	}

	public static void applySave(String str) {// Aplica o save carregado no jogo
		String[] spl = str.split("/");
		for (int i = 0; i < spl.length; i++) {
			String[] spl2 = spl[i].split("->");
			switch (spl2[0]) {
			case "level":
				World.restartGame("level" + spl2[1] + ".png");
				Game.gameState = "NORMAL";
				break;
			case "vida":
				Game.player.life = Integer.parseInt(spl2[1]);
				break;
			case "ammo":
				Game.player.ammo = Integer.parseInt(spl2[1]);
				break;
			case "x":
				Game.player.setX(Integer.parseInt(spl2[1]));
				break;
			case "y":
				Game.player.setY(Integer.parseInt(spl2[1]));
				break;
			case "pontos":
				Game.pontos = (Integer.parseInt(spl2[1]));
				break;
			}
		}
	}

	public static String loadGame(int encode) {// Carrega o arquivo de save
		String line = "";
		File file = new File("save.txt");
		if (file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					while ((singleLine = reader.readLine()) != null) {
						String[] trans = singleLine.split("->");
						char[] val = trans[1].toCharArray();
						trans[1] = "";
						for (int i = 0; i < val.length; i++) {
							val[i] -= encode;
							trans[1] += val[i];
						}
						line += trans[0];
						line += "->";
						line += trans[1];
						line += "/";
					}
				} catch (IOException e) {
				}
			} catch (FileNotFoundException e) {
			}
		}
		return line;
	}

	public static void saveGame(String[] val1, int[] val2, int encode) {// Salva o jogo em um arquivo de save
		BufferedWriter write = null;
		try {
			write = new BufferedWriter(new FileWriter("save.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < val1.length; i++) {
			String current = val1[i];
			current += "->";
			char[] value = Integer.toString(val2[i]).toCharArray();
			for (int n = 0; n < value.length; n++) {
				value[n] += encode;
				current += value[n];
			}
			try {
				write.write(current);
				if (i < val1.length - 1) {
					write.newLine();
				}
			} catch (IOException e) {

			}
		}
		try {
			write.flush();
			write.close();
		} catch (IOException e) {

		}
	}

	/************ Renderiza��o ************/

	public void render(Graphics g) {
		// Criar background transparente
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(new Color(0, 0, 0, 100));

		// Cria o titulo do menu
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 70));
		g.drawString("Lumber Jack", ((Game.WIDTH * Game.SCALE) / 2) - 240, ((Game.HEIGHT * Game.SCALE) / 2) - 150);

		// Escolhe a cor da fonte do Menu e Dificuldade
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 36));

		// Cria as op��es do menu
		if (!isDific) { // Se n�o estiver escolhendo dificuldade
			if (pause == false) {// Se o jogo n�o foi pausado 
				g.drawString("Novo Jogo", ((Game.WIDTH * Game.SCALE) / 2) - 150,
						((Game.HEIGHT * Game.SCALE) / 2) + 150);
			} else {// Se o jogo foi pausado 
				g.drawString("Continuar", ((Game.WIDTH * Game.SCALE) / 2) - 150,
						((Game.HEIGHT * Game.SCALE) / 2) + 150);
			}
			g.drawString("Carregar Jogo", ((Game.WIDTH * Game.SCALE) / 2) - 150,
					((Game.HEIGHT * Game.SCALE) / 2) + 200);
			g.drawString("Sair", ((Game.WIDTH * Game.SCALE) / 2) - 150, ((Game.HEIGHT * Game.SCALE) / 2) + 250);

			// Cria o selecionador de op��es
			if (options[currentOption].equals("Novo Jogo")) {
				g.drawString(">", ((Game.WIDTH * Game.SCALE) / 2) - 180, ((Game.HEIGHT * Game.SCALE) / 2) + 150);
			} else if (options[currentOption].equals("Carregar Jogo")) {
				g.drawString(">", ((Game.WIDTH * Game.SCALE) / 2) - 180, ((Game.HEIGHT * Game.SCALE) / 2) + 200);
			} else if (options[currentOption].equals("Sair")) {
				g.drawString(">", ((Game.WIDTH * Game.SCALE) / 2) - 180, ((Game.HEIGHT * Game.SCALE) / 2) + 250);
			}
		} else if (isDific) {// Se estiver escolhendo dificuldades
			// Cria as op��es de dificuldade
			g.drawString("Easy", ((Game.WIDTH * Game.SCALE) / 2) - 150, ((Game.HEIGHT * Game.SCALE) / 2) + 150);
			g.drawString("Medium", ((Game.WIDTH * Game.SCALE) / 2) - 150, ((Game.HEIGHT * Game.SCALE) / 2) + 200);
			g.drawString("Hard", ((Game.WIDTH * Game.SCALE) / 2) - 150, ((Game.HEIGHT * Game.SCALE) / 2) + 250);
			// Cria o selecionador de dificuldade
			if (difficult[currentDific].equals("EASY")) {
				g.drawString(">", ((Game.WIDTH * Game.SCALE) / 2) - 180, ((Game.HEIGHT * Game.SCALE) / 2) + 150);
			} else if (difficult[currentDific].equals("MEDIUM")) {
				g.drawString(">", ((Game.WIDTH * Game.SCALE) / 2) - 180, ((Game.HEIGHT * Game.SCALE) / 2) + 200);
			} else if (difficult[currentDific].equals("HARD")) {
				g.drawString(">", ((Game.WIDTH * Game.SCALE) / 2) - 180, ((Game.HEIGHT * Game.SCALE) / 2) + 250);
			}
		}
	}

	/************************************/
}
