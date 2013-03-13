import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Michal
 */
public class Player 
{
    public static final int NUMBER_OF_L_SHAPE_ROTATE_POSIBILITIES = 3;
    public static final int NUMBER_OF_DOT_SHAPE_ROTATE_POSIBILITIES = 0;
    
    private Pawn lShapePawn, dotShapePawn;
    final public int _id;
    
    public Player(int id)
    {
        //dotShapePawn    = new DotShapePawn(id);
        lShapePawn      = new LShapePawn(id);
    
        lShapePawn.PrintAllConfigurations();
        _id = id;

    }
    
    public void go()
    {
        //
    }
}

// 


abstract class Pawn
{
    public final static int SMALL_BOARD_SIZE = 3;
    
    protected final int     _id;
    protected List<int[][]> listOfPawnConfigurations;
    protected int           configurationInUse;
    
    public Pawn(int id)
    {
        listOfPawnConfigurations = new ArrayList<>();
        listOfPawnConfigurations.add(new int[SMALL_BOARD_SIZE][SMALL_BOARD_SIZE]);
        
        configurationInUse = 0; // domyślna wartość to 0 
        _id = id;

        InitPawnBoardAndRotatePosibilities(id);
    }
    
    public void PrintAllConfigurations()
    {
        for(int i=0; i < listOfPawnConfigurations.size(); ++i)
        {
            System.out.println("Conf" + i + "= ");
            Print(listOfPawnConfigurations.get(i));
            System.out.println();
        }
    }
    
    public void Print(int[][] matrixToPrint)
    {
        for ( int[] row : matrixToPrint ) 
        {
            System.out.println(Arrays.toString(row));
        }
    }
    
    public int[][] RotateCW(int[][] boardToRotate)
    {
        // if jesli więćej nic 4 obroty?
        
        int[][] tmp = new int[SMALL_BOARD_SIZE][SMALL_BOARD_SIZE];
        
        for (int i = 0; i < SMALL_BOARD_SIZE; i++) 
        {
            for (int j = 0; j < SMALL_BOARD_SIZE; j++) 
            {
                tmp[i][j] = boardToRotate[SMALL_BOARD_SIZE-j-1][i];
            }
        }
        
//            System.out.println("Conf = ");
//            for ( int[] row : tmp ) 
//            {
//                System.out.println(Arrays.toString(row));
//            }
//            System.out.println();
        
        return tmp;
    }
    
    public int GetAmoutOfRotatePosibilities()
    {
        return listOfPawnConfigurations.size();
    }
    
    abstract public void InitPawnBoardAndRotatePosibilities(int id);
}


class DotShapePawn extends Pawn
{
    public DotShapePawn(int id)
    {
        super(id);
    }
    
    @Override
    public void InitPawnBoardAndRotatePosibilities(int id)
    {
        listOfPawnConfigurations.get(configurationInUse)[1][1] = id;
        
        try
        {
            if(listOfPawnConfigurations.size() != 1)
                throw new Exception("[Błąd], możliwości obrotu w dotShape != 1");
        }
        catch(Exception ex)
        {
            System.err.println(ex.getMessage());
        }
    }
}

class LShapePawn extends Pawn
{
    public LShapePawn(int id)
    {
        super(id);
    }
    
    @Override
    public void InitPawnBoardAndRotatePosibilities(int id) 
    {
        listOfPawnConfigurations.get(configurationInUse)[0][1]= id;
        listOfPawnConfigurations.get(configurationInUse)[1][1]= id;
        listOfPawnConfigurations.get(configurationInUse)[2][1]= id;
        listOfPawnConfigurations.get(configurationInUse)[2][0]= id;
        
        int[][] tmp = new int[SMALL_BOARD_SIZE][SMALL_BOARD_SIZE];
        
        for(int i=0; i<Player.NUMBER_OF_L_SHAPE_ROTATE_POSIBILITIES; ++i)
        {
            // tworzenie listy wszystkich możliwych układów ułożenia pionka (w tablicy 3x3)
            // 1. pobierany indeks ostatniego układu
            // 2. pobieranie tablicy pod znalezionych indeksem
            // 3. obrót tablicy pod znalezionym indeksem
            // 4. dodanie bitmapy / tablicy 3x3 z ułożeniem pionka to możliwych konfiguracji
            listOfPawnConfigurations.add( RotateCW(listOfPawnConfigurations.get(i)) );   
        }
     } 
}