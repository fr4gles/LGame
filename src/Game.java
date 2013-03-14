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
    public final static int BOARD_SIZE = 4;
    public final static int PLAYERS_QUANTITY = 2;
    public static int[][] board; // plansza
//  prywatne
    
    private final int[][] empty_board;
    
    private List<Player> players;

    public static void main(String[] args)
    {
       Game game = new Game(); 
       game.PrintBoard();
    }

    public Game() 
    {
        Game.board = new int[BOARD_SIZE][BOARD_SIZE]; // wyzerowana na wejściu
        
        this.empty_board = new int[BOARD_SIZE][BOARD_SIZE];
        
        players = new ArrayList<>();
        
        AddPlayers();
        
//        players.add(new Player(new Random().nextInt(10/*Integer.MAX_VALUE*/)+1,0));
//        players.add(new Player(new Random().nextInt(10/*Integer.MAX_VALUE*/)+1,2));
        
        players.get(0).GetPawns().get(0).SetPosition(new Position(2,2));
        players.get(0).GetPawns().get(1).SetPosition(new Position(3,0));
        
        players.get(1).GetPawns().get(0).SetPosition(new Position(1,1));
        players.get(1).GetPawns().get(1).SetPosition(new Position(0,3));
        
        Collections.shuffle(players);
        
//        int number_of_movements = 0;
//        while(Play() == true)
//        {
//            ++number_of_movements;
//        }
//        System.out.println("Ilość ruchów = "+number_of_movements);
        
        
        
//        DownPawnOnBoard(players.get(0).GetPawns().get(0));
//        DownPawnOnBoard(players.get(0).GetPawns().get(1));
//        
//        DownPawnOnBoard(players.get(1).GetPawns().get(0));
//        DownPawnOnBoard(players.get(1).GetPawns().get(1));
    }
    
    public boolean Play()
    {
//        int i=0;
        boolean wasPreviouslyMoved = false;
        for(Player x: players)
        {
            wasPreviouslyMoved = x.IsMoved();
            
            x.go();
            
            if(x.IsMoved() != wasPreviouslyMoved)
                continue;
            else
                return false;
        }
        return true;
    }
    
    public void DownPawnOnBoard(Pawn p)
    {
        int x_pos = p.GetPosition().x;
        int y_pos = p.GetPosition().y;
        
        for(int i = x_pos - 1, x = 0; i < x_pos + 2 && x < Pawn.SMALL_BOARD_SIZE; ++i, ++x)
        {
            if(i>-1)
            {
                for(int j = y_pos - 1, y=0; j < y_pos + 2 && y < Pawn.SMALL_BOARD_SIZE; ++j, ++y)
                {
                    if(j>-1)
                    {
                        if( (p.GetlistOfPawnConfigurations().get(p.GetConfInUse()))[x][y] != 0)
                        {
                            board[i][j] =  (p.GetlistOfPawnConfigurations().get(p.GetConfInUse()))[x][y];
                        }
                    }
                }
            }
        }
    }
    
    public void UpPawnFromBoard(Pawn p)
    {
        int x_pos = p.GetPosition().x;
        int y_pos = p.GetPosition().y;
        
        for(int i = x_pos - 1, x = 0; i < x_pos + 2 && x < Pawn.SMALL_BOARD_SIZE; ++i, ++x)
        {
            if(i>-1)
            {
                for(int j = y_pos - 1, y=0; j < y_pos + 2 && y < Pawn.SMALL_BOARD_SIZE; ++j, ++y)
                {
                    if(j>-1)
                    {
                        if( (p.GetlistOfPawnConfigurations().get(p.GetConfInUse()))[x][y] == p.GetID())
                        {
                            board[i][j] =  0;
                        }
                    }
                }
            }
        }
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
            players.add(new Player(uniqueID.nextInt(50/*Integer.MAX_VALUE*/)+1,i+i));
        }   
    }
    

}
