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
public class Game 
{
//  publiczne 
    public final static int BOARD_SIZE = 4;
    public final static int PLAYERS_QUANTITY = 2;
    
//  prywatne
    private int[][] board; // plansza
    
    private List<Player> players;

    public static void main(String[] args)
    {
       Game game = new Game(); 
       game.PrintBoard();
    }

    public Game() 
    {
        this.board = new int[BOARD_SIZE][BOARD_SIZE]; // wyzerowana na wejściu
        players = new ArrayList<>();
        
        AddPlayers();
                
        // losowanie zaczynającego playera, 50% szans na każdego w przypadku dwóch graczy
        Collections.shuffle(players);
    }
    
    public void PrintBoard()
    {
        for( int[] i : board )
        {
            System.out.println(Arrays.toString(i));
        }
    }
    
    private void AddPlayers()
    {
        Random uniqueID = new Random();
        for(int i=0;i<PLAYERS_QUANTITY; ++i)
        {
            // losowane jest UID od 1 do ...
            players.add(new Player(uniqueID.nextInt(10/*Integer.MAX_VALUE*/)+1));
        }   
    }
    

}
