import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Michal Franczyk
 * @date 13.03.2013
 */

/**
 * Klasa Gry
 */
public final class Game 
{
//  publiczne 
    public final static boolean TEST = false;           // true - wypisuje dane dodatkowe, false - nie wypisuje
    public final static int     BOARD_SIZE = 4;         // wielkość planszy
    public final static int     PLAYERS_QUANTITY = 2;   // ilość graczy
    public final static int     PAWNS_QUANTITY = 2;     // ilość pionków przypadająca na gracza
    public static int[][]       board;                  // plansza 4x4
    public static int           number_of_movements;    // ilość ruchów
    
//  prywatne      
    private List<Player>        players;                // lista graczy
    

    /**
     * Main -> tworzony obiekt gry, rozpoczynana rozgrywka
     * @param args
     */
    public static void main(String[] args)
    {
        Game game = new Game(); 
        
        number_of_movements = 0;
        while(game.Play() == true);  // dopóki true - graj
        System.out.println("Ilosc ruchow = "+number_of_movements);
        
        if(TEST)
        {
            System.out.println("= = = = = KONIEC = = = = = = =");
            Game.PrintBoard();
        }
    }

    /**
     * publiczny konstrukor
     * inicjalizuje grę
     */
    public Game() 
    {
        Game.board          = new int[BOARD_SIZE][BOARD_SIZE]; // wyzerowana na wejściu
        players             = new ArrayList<>();
        
        AddPlayers();                   // dodawanie graczy
        
        Collections.shuffle(players);   // losowanie kolejności
        
        InitPlayers();                  // inicjalizacja graczy wartościami początkowymi (głownie pozycja i konfiguracja)
        
        if(TEST)
        {
            System.out.println("= = = = = = = = = = = = = = =");
            System.out.println("= = = = = START = = = = = = =");
            System.out.println("= = = = = = = = = = = = = = =");
            System.out.println(">>> - - Init Board: - - <<<");
            PrintBoard();
            System.out.println("- - - - - - - - - - -\n");
        }
    }
    
    
    /**
     * publiczna
     * Funckja odpowiedzialna za pozwolenie graczom dalszych ruchów
     * @return true - można grać dalej, false - gra skonczona (któryś z graczy nie może się ruszyć)
     */
    public boolean Play()
    {
        int wasPreviouslyMoved = 0; // pamięć poprzedniej ilości ruchów
        for(Player player: players) // każdy gracz czeka na swoją kolej
        {
            wasPreviouslyMoved = number_of_movements; // tworzenie kopii ilości ruchów
            
            player.go();    // czeka na ruch gracza, gracz zwiększa ilość ruchów o 1 jak się może ruszyć
            
            if(number_of_movements != wasPreviouslyMoved) // jeśli wartości są różne to gracz się ruszył
                continue; // przejście do następnego gracza
            else
                return false; // gracz nie mógł się ruszyć, zwróć false
        }
        return true; // wszyscy gracze się ruszyli, zwróć true
    }
       
    
    /**
     *  statyczna, publiczna
     *  metoda służąca do testowego wypisu planszy
     */
    public static void PrintBoard()
    {
        for( int[] row : board )
            System.out.println(Arrays.toString(row));
    }
    
    /**
     * prywatna
     * służy do inicjalizacji początkowego położenia graczy oraz ich konfiguracji
     */
    private void InitPlayers()
    {
        // dodanie pozycji oraz konfiguracji
        players.get(0).GetPawns().get(0).SetPosition(new Position(2,2,0));
        players.get(0).GetPawns().get(1).SetPosition(new Position(3,0,0));
        
        players.get(1).GetPawns().get(0).SetPosition(new Position(1,1,2));
        players.get(1).GetPawns().get(1).SetPosition(new Position(0,3,0));
        
        // położenie pionków na planszę
        Player.DownPawnOnBoard(players.get(0).GetPawns().get(0));
        Player.DownPawnOnBoard(players.get(0).GetPawns().get(1));
        
        Player.DownPawnOnBoard(players.get(1).GetPawns().get(0));
        Player.DownPawnOnBoard(players.get(1).GetPawns().get(1));
    }
    
    /**
     *  prywatna
     * służy do dodawania graczyz unikalnym id (0,max int)
     */
    private void AddPlayers()
    {
        Random uniqueID = new Random();
        for(int i=0;i<PLAYERS_QUANTITY; ++i)
        {
            // losowane jest UID od 1 do ...
            players.add(new Player(uniqueID.nextInt(Integer.MAX_VALUE-1)+1));
        }   
    }
}
