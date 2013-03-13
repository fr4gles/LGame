import java.util.ArrayList;
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
    
    private List<Player> _players;

    public static void main(String[] args)
    {
       Game game = new Game(); 
       //game.PrintBoard();
    }

    public Game() 
    {
        this.board = new int[BOARD_SIZE][BOARD_SIZE]; // wyzerowana na wejściu
        _players = new ArrayList<>();
        
        Random uniqueID = new Random();
        
        for(int i=0;i<PLAYERS_QUANTITY; ++i)
        {
            _players.add(new Player(uniqueID.nextInt(1000/*Integer.MAX_VALUE*/)));
        }    
    }
    
    public void PrintBoard()
    {
        for( int[] i : board )
        {
            for(int j : i)
                System.out.print(j);
            System.out.println();
        }
    }
    
    private boolean CheckIdUniquity(List<Player> listToBeChecked)
    {
        ///
        
        return false;
    }
}
