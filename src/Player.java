import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Michal
 */
public class Player 
{
    public static final int NUMBER_OF_L_SHAPE_FLIP_POSIBILITIES = 2;
    public static final int NUMBER_OF_L_SHAPE_ROTATE_POSIBILITIES = 4;
    
    public static final int NUMBER_OF_DOT_SHAPE_ROTATE_POSIBILITIES = 0;
    public static final int NUMBER_OF_DOT_SHAPE_FLIP_POSIBILITIES = 0;
    
    private List<Pawn> pawnsList;
    private int canMove;
    
    final public int _id;
    
    
    public Player(int id, int configuration)
    {
        pawnsList = new ArrayList<>();

        pawnsList.add(new LShapePawn(id,configuration));
        pawnsList.add(new DotShapePawn(id));
        
        //pawnsList.get(0).PrintAllConfigurations();
        //pawnsList.get(1).PrintAllConfigurations();

        _id = id;
        canMove = 0;
    }
    
    public void go()
    {
        int i = 0,
            check = canMove;
       
        if(SearchPlaceForPawn(pawnsList.get(i++)) == true)
            ++canMove; 
        
        if(check != canMove)
            SearchPlaceForPawn(pawnsList.get(i));
        
    }
    
    public boolean SearchPlaceForPawn(Pawn p)
    {
        // znalezienie możliwych miejsc do położenia
        // losowanie miejsca
        // wybór miejsca
        // aktualizacja position
        
        int x_pos = 0;
        int y_pos = 0;
        
        int tmp = 0;
        
        for(int ix = 0; ix < Game.BOARD_SIZE; ++ix)
        {
            for(int iy = 0; iy < Game.BOARD_SIZE; ++iy)
            {
                x_pos = ix;
                y_pos = iy;
                
                for(int c = 0; c < p.GetAmoutOfRotatePosibilities(); ++c)
                {
                    for(int i = x_pos - 1, x = 0; i < x_pos + 2 && x < Pawn.SMALL_BOARD_SIZE; ++i, ++x)
                    {
                        if(i>-1 && i < 4)
                            for(int j = y_pos - 1, y=0; j < y_pos + 2 && y < Pawn.SMALL_BOARD_SIZE; ++j, ++y)
                            {
                                if(j>-1 && j < 4)
                                {
                                    //tmp = (p.GetlistOfPawnConfigurations().get(p.GetConfInUse()))[x][y];
                                    if( (p.GetlistOfPawnConfigurations().get(c))[x][y] != 0 )
                                    {
                                        if(Game.board[i][j] == 0);
                                            return false;
            //                            board[i][j] =  (p.GetlistOfPawnConfigurations().get(p.GetConfInUse()))[x][y];
                                    }
                                }
                            }
                    }
                }
            }
        }
        
        return true;
    }
    
    public void SetPawns(List<Pawn> list)
    {
        this.pawnsList = list;
    }
    
    public List<Pawn> GetPawns()
    {
        return pawnsList;
    }
    
    public int isMoved()
    {
        return canMove;
    }
}
/*******************************************************************/


/*******************************************************************/
/*******************************************************************/ 
class Position
{
    public int x, y;

    public Position() 
    {
        this.x  = -1;
        this.y  = -1;
    }
   
    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}


/*******************************************************************/
/*******************************************************************/ 
abstract class Pawn
{
    public final static int SMALL_BOARD_SIZE = 3;
    
    protected final int     _id;
    protected List<int[][]> listOfPawnConfigurations;
    protected int           configurationInUse;
    
    protected Position      position;
    
    public Pawn(int id)
    {
        listOfPawnConfigurations = new ArrayList<>();
        listOfPawnConfigurations.add(new int[SMALL_BOARD_SIZE][SMALL_BOARD_SIZE]);
        
        configurationInUse = 0; // domyślna wartość to 0 
        _id = id;
        
        position =  new Position();
        
        // sprawdzić czy ma sens .... w klasie position jest -1
        position.x = position.y = -1;   // początkowe przypisanie pozycji 

        InitRotateAndFlipPawnPosibilities(id);
    }
    
    public void PrintAllConfigurations()
    {
        System.out.println("----- "+_id+" -----");
        for(int i=0; i < listOfPawnConfigurations.size(); ++i)
        {
            System.out.println("Conf" + i + "= ");
            Print(listOfPawnConfigurations.get(i));
            System.out.println();
        }
        System.out.println("---------------");
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
        return tmp;
    }
    
    public int[][] FlipHorizontal(int[][] boardToFlip)
    {
        int[][] tmp = new int[SMALL_BOARD_SIZE][SMALL_BOARD_SIZE];
        for(int i = 0; i < SMALL_BOARD_SIZE; ++i)
        {
            for(int j = 0; j < SMALL_BOARD_SIZE; ++j)
            {
                tmp[SMALL_BOARD_SIZE-i-1][j] = boardToFlip[i][j];
            }
        }        
        return tmp;
    }
    
    public int[][] FlipVertical(int[][] boardToFlip)
    {
        int[][] tmp = new int[SMALL_BOARD_SIZE][SMALL_BOARD_SIZE];
        for(int i = 0; i < SMALL_BOARD_SIZE; ++i)
        {
            for(int j = 0; j < SMALL_BOARD_SIZE; ++j)
            {
                tmp[i][SMALL_BOARD_SIZE-j-1] = boardToFlip[i][j];
            }
        }        
        return tmp;
    }
    
    public void FillListOfPawnConfigurations(int start, int stop)
    {
        for(int i = start; i<stop; ++i)
        {
            // 1. pobierany indeks ostatniego układu
            // 2. pobieranie macierzy pod znalezionych indeksem
            // 3. obrót macierzy pod znalezionym indeksem
            // 4. dodanie bitmapy / tablicy 3x3 z nowym ułożeniem pionka to możliwych konfiguracji
            listOfPawnConfigurations.add( RotateCW(listOfPawnConfigurations.get(i)) );   
        }
    }
    
    public int GetAmoutOfRotatePosibilities()
    {
        return this.listOfPawnConfigurations.size();
    }
    
    public void SetPosition(Position pos)
    {
        this.position = pos;
    }
    
    public Position GetPosition()
    {
        return this.position;
    }
    
    public int[][] GetPawnConfiguration(int index)
    {
        return this.listOfPawnConfigurations.get(index);
    }
    
    public List<int[][]> GetlistOfPawnConfigurations()
    {
        return this.listOfPawnConfigurations;
    }
    
    public int GetConfInUse()
    {
        return this.configurationInUse;
    }
    abstract public void InitRotateAndFlipPawnPosibilities(int id);
}


/*******************************************************************/
/*******************************************************************/ 
class DotShapePawn extends Pawn
{
    public DotShapePawn(int id)
    {
        super(id);
    }
    
    @Override
    public void InitRotateAndFlipPawnPosibilities(int id)
    {
        // wprowadzenie początkowych danych, dot shape
        listOfPawnConfigurations.get(configurationInUse)[1][1] = id;
        
        // tutaj nie jest potrzebne obrananie
        // zatem nie m sensu generowac możliwych pozycji
        try
        {
            if(listOfPawnConfigurations.size() != 1)
                throw new Exception("[Błąd#1], możliwości obrotu w dotShape nie mogą być != 1");
        }
        catch(Exception ex)
        {
            System.err.println(ex.getMessage());
        }
    }
}

/*******************************************************************/
/*******************************************************************/ 
class LShapePawn extends Pawn
{
    public LShapePawn(int id, int configuration)
    {
        super(id);
        this.configurationInUse = configuration;
    }
    
    @Override
    public void InitRotateAndFlipPawnPosibilities(int id) 
    {
        // wprowadzenie początkowych danych, L shape
        listOfPawnConfigurations.get(configurationInUse)[0][1]= id;
        listOfPawnConfigurations.get(configurationInUse)[1][1]= id;
        listOfPawnConfigurations.get(configurationInUse)[2][1]= id;
        listOfPawnConfigurations.get(configurationInUse)[2][0]= id;
        
        
        {   // tworzenie listy wszystkich możliwych układów ułożenia pionka (w tablicy 3x3)
            FillListOfPawnConfigurations(0, Player.NUMBER_OF_L_SHAPE_ROTATE_POSIBILITIES-1);

            // 1. pobierany indeks pierwszego w układzie
            // 2. pobieranie macierzy pod znalezionych indeksem
            // 3. odbicie w pionie macierzy pod znalezionym indeksem
            // 4. dodanie bitmapy / tablicy 3x3 z nowym ułożeniem pionka to możliwych konfiguracji
            listOfPawnConfigurations.add( FlipVertical(listOfPawnConfigurations.get(0)) );

            FillListOfPawnConfigurations(listOfPawnConfigurations.size() - 1, Player.NUMBER_OF_L_SHAPE_ROTATE_POSIBILITIES-1 + listOfPawnConfigurations.size() - 1);
        }
    } 
}