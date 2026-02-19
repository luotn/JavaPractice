package day3;

import java.util.ArrayList;
import java.util.Scanner;

public class GuessMain {
    public static void main(String[] args) {
        int PLAYGROUND = 100;
        int start = 1;
        int end = 100;
        ArrayList<GuessGame> gamesList = new ArrayList<>();

        for (int i = 0; i < PLAYGROUND; i++) {
            GuessGame game = new GuessGame(i, start, end, Methods.BINARY);
            gamesList.add(game);
        }
        System.out.println(PLAYGROUND + " games created, starting games...");
        ArrayList<Thread> gameThreadsList = new ArrayList<>();
        for (GuessGame game: gamesList) {
            Thread thread = new Thread(game);
            gameThreadsList.add(thread);
            thread.start();
        }

        System.out.println("Games started, waiting all games to finish...");

//      I know it's stupid to suspend main thread, but it has nothing else to do anyways, stfu.
        for(Thread gameThread: gameThreadsList) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Simulation done!");


        int totalGuesses = 0;
        int totalJackpot = 0;
        for (GuessGame game: gamesList) {
            totalGuesses += game.GUESSCOUNT;
            totalJackpot += game.GUESSCOUNT == 1 ? 1 : 0;
        }
        System.out.println("Average " + 1.0 * totalGuesses / PLAYGROUND + " guesses per game.");
        System.out.println("There are " + totalJackpot + " spot on guess game(s).");

        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("----------\n" +
                    "What do you want to do next?\n" +
                    "1) Show first game guesses.\n" +
                    "2) Show best game guess(es).\n" +
                    "3) Show worst game guess(es).\n" +
                    "4) Show ALL games guesses.\n" +
                    "0) Exit.");
            int option = input.nextInt();
            switch (option) {
                case 1 -> System.out.println(gamesList.get(0));
                case 2 -> System.out.println("WIP");
                case 3 -> System.out.println("WIP");
                case 4 -> System.out.println("WIP");
                case 0 -> System.exit(0);
            }
        }
    }
}