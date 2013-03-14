import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 *
 * @author Michal Franczyk
 * @date 13.03.2013
 */
public final class Game 
{
//  publiczne 
    public final static boolean TEST = false;
    public final static int BOARD_SIZE = 4;
    public final static int PLAYERS_QUANTITY = 2;
    public static int[][] board; // plansza
//  prywatne
    
    private final int[][] empty_board;
    
    private List<Player> players;

    public static void main(String[] args)
    {
        Game game = new Game(); 
        if(TEST)
        {
            System.out.println("= = = = = KONIEC = = = = = = =");
            Game.PrintBoard();
        }
    }

    public Game() 
    {
        Game.board = new int[BOARD_SIZE][BOARD_SIZE]; // wyzerowana na wejściu
        
        this.empty_board = new int[BOARD_SIZE][BOARD_SIZE];
        
        players = new ArrayList<>();
        
        AddPlayers();
        
        Collections.shuffle(players);
        
        InitPlayers();
        
        if(TEST)
        {
            System.out.println("= = = = = = = = = = = = = = =");
            System.out.println("= = = = = START = = = = = = =");
            System.out.println("= = = = = = = = = = = = = = =");
            System.out.println(">>> - - Init Board: - - <<<");
            PrintBoard();
            System.out.println("- - - - - - - - - - -\n");
        }
        
        int number_of_movements = 0;
        while(Play() == true)
        {
            ++number_of_movements;
        }
        System.out.println("Ilosc ruchow = "+number_of_movements);
        
    }
    
    public boolean Play()
    {
        boolean wasPreviouslyMoved = false;
        for(Player x: players)
        {
            wasPreviouslyMoved = x.IsMoved();
            
            x.go();
            
            if(x.IsMoved() != wasPreviouslyMoved)
            {
                x.ResetIsMoved();
                continue;
            }
            else
                return false;
        }
        return true;
    }
       
    
    public static void PrintBoard()
    {
        for( int[] i : board )
        {
            System.out.println(Arrays.toString(i));
        }
    }
    
    private void InitPlayers()
    {
        players.get(0).GetPawns().get(0).SetPosition(new Position(2,2,0));
        players.get(0).GetPawns().get(1).SetPosition(new Position(3,0,0));
        
        players.get(1).GetPawns().get(0).SetPosition(new Position(1,1,2));
        players.get(1).GetPawns().get(1).SetPosition(new Position(0,3,0));
        
        players.get(0).DownPawnOnBoard(players.get(0).GetPawns().get(0));
        players.get(0).DownPawnOnBoard(players.get(0).GetPawns().get(1));
        
        players.get(1).DownPawnOnBoard(players.get(1).GetPawns().get(0));
        players.get(1).DownPawnOnBoard(players.get(1).GetPawns().get(1));
    }
    
    private void AddPlayers()
    {
        Random uniqueID = new Random();
        for(int i=0;i<PLAYERS_QUANTITY; ++i)
        {
            // losowane jest UID od 1 do ...
            players.add(new Player(uniqueID.nextInt(98/*Integer.MAX_VALUE-1*/)+1));
        }   
    }
    

}
