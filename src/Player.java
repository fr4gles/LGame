import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Michal
 */
public class Player 
{
    public static final int NUMBER_OF_L_SHAPE_FLIP_POSIBILITIES     = 2;
    public static final int NUMBER_OF_L_SHAPE_ROTATE_POSIBILITIES   = 4;
    
    public static final int NUMBER_OF_DOT_SHAPE_ROTATE_POSIBILITIES = 0;
    public static final int NUMBER_OF_DOT_SHAPE_FLIP_POSIBILITIES   = 0;
    
    
    private List<Pawn>      pawnsList;
    private int             currentPawn;
    private boolean         moved;
    
    final public int        _id;
    
    public Player(int id)
    {
        pawnsList   = new ArrayList<>();

        _id         = id;
        currentPawn = 0;
        moved       = false;

        pawnsList.add(new LShapePawn(id));
        pawnsList.add(new DotShapePawn(id));
        
        if(Game.TEST)
        {
            System.out.println("- - Configuration Board: - -");
            pawnsList.get(0).PrintAllConfigurations();
            pawnsList.get(1).PrintAllConfigurations();
            System.out.println("- - - - - - - - - - -\n");
        }
    }
    
    public void go()
    {
        currentPawn = 0;
        
        moved       = false;
        
        UpPawnFromBoard(pawnsList.get(currentPawn));
        if( SearchPlaceForPawn(pawnsList.get(currentPawn)) )
        {
            moved = true;
            DownPawnOnBoard(pawnsList.get(currentPawn));
        }
        
        ++currentPawn;
        
        if(moved == true)
        {
            UpPawnFromBoard(pawnsList.get(currentPawn));
            SearchPlaceForPawn(pawnsList.get(currentPawn));
            DownPawnOnBoard(pawnsList.get(currentPawn));
            
            ++Game.number_of_movements;
        }
        
        if(Game.TEST)
            Game.PrintBoard();
    }
    
    public boolean SearchPlaceForPawn(Pawn p)
    {
        // znalezienie możliwych miejsc do położenia
        // losowanie miejsca
        // wybór miejsca
        // aktualizacja position
        // jeśli brak powodzenia - revert
        
        int goodPositions               = 0,
            tmpX                        = p.GetPosition().x,
            tmpY                        = p.GetPosition().y,
            tmpC                        = p.GetPosition().conf,
            tmpNumberOfOccupiedFields   = p.GetNumberOfOccupiedFields(),
            tempConfiguration[][];
        
        List<Position> goodMove = new ArrayList<>();
        
        for(int c = 0; c < p.GetAmoutOfRotatePosibilities(); ++c)
        {
            tempConfiguration = p.GetListOfPawnConfigurations().get(c);
            for(int xpos = 0; xpos < Game.BOARD_SIZE; ++xpos)
            {
                for(int ypos = 0; ypos < Game.BOARD_SIZE; ++ypos)
                {
                    goodPositions = 0;

                    if( ( (xpos == tmpX) && (ypos == tmpY) ) && (c == tmpC) )
                        continue;

                    for(int i = xpos - 1, x = 0; i < xpos + 2 && x < Pawn.SMALL_BOARD_SIZE; ++i, ++x)
                        if(i>-1 && i<4)
                            for(int j = ypos - 1, y=0; j < ypos + 2 && y < Pawn.SMALL_BOARD_SIZE; ++j, ++y)
                                if(j>-1 && j<4)
                                    if( (tempConfiguration[x][y] != 0) && (Game.board[i][j] == 0) )
                                            ++goodPositions;

                    if(goodPositions == tmpNumberOfOccupiedFields)
                    {
                        goodMove.add( new Position(xpos, ypos, c));
                        if(Game.TEST)
                            System.out.println(goodMove.get(goodMove.size()-1));
                    }
                }
            }
        }
        
        if(!goodMove.isEmpty())
        {
            Random randPosition = new Random();
            pawnsList.get(currentPawn).SetPosition( goodMove.get( randPosition.nextInt(goodMove.size()) ) );
            
            if(Game.TEST)
                System.out.println(" - > "+pawnsList.get(currentPawn).GetPosition());
            
            return true;
        }
        else
        {
            DownPawnOnBoard(p); // revert changes
            
            if(Game.TEST)
                System.out.println("- - REVERT CHANGES - -");
            
            return false;
        }
    }
    
    public void UpPawnFromBoard(Pawn p)
    {
        int x_pos                   = p.GetPosition().x,
            y_pos                   = p.GetPosition().y,
            tempConfiguration[][]   = p.GetListOfPawnConfigurations().get(p.GetPosition().conf),
            tempID                  = p.GetID();
 
        for(int i = x_pos - 1, x = 0; i < x_pos + 2 && x < Pawn.SMALL_BOARD_SIZE; ++i, ++x)
            if(i>-1 && i<4)
                for(int j = y_pos - 1, y=0; j < y_pos + 2 && y < Pawn.SMALL_BOARD_SIZE; ++j, ++y)
                    if(j>-1 && j<4)
                        if( tempConfiguration[x][y] == tempID)
                            Game.board[i][j] =  0;
    }
    
    public static void DownPawnOnBoard(Pawn p)
    {
        int x_pos                   = p.GetPosition().x,
            y_pos                   = p.GetPosition().y,
            tempConfiguration[][]   = p.GetListOfPawnConfigurations().get(p.GetPosition().conf);
        
        for(int i = x_pos - 1, x = 0; i < x_pos + 2 && x < Pawn.SMALL_BOARD_SIZE; ++i, ++x)
            if(i>-1 && i<4)
                for(int j = y_pos - 1, y=0; j < y_pos + 2 && y < Pawn.SMALL_BOARD_SIZE; ++j, ++y)
                    if(j>-1 && j<4)
                        if( tempConfiguration[x][y] != 0)
                            Game.board[i][j] =  tempConfiguration[x][y];
    }

    
    public void SetPawns(List<Pawn> list)
    {
        this.pawnsList = list;
    }
    
    public List<Pawn> GetPawns()
    {
        return pawnsList;
    }
}
/*******************************************************************/


/*******************************************************************/
/*******************************************************************/ 
class Position
{
    // x, y współrzędne
    // conf konfiguracja pionka
    public int x, y, conf;

    public Position() 
    {
        this.x  = -1;
        this.y  = -1;
        this.conf = 0;
    }
   
    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.conf = 0;
    }
    
    public Position(int x, int y, int c)
    {
        this.x = x;
        this.y = y;
        this.conf = c;
    }
    
    public String toString() 
    {
        return("["+x+" "+y+" "+conf+"]");
    }
}


/*******************************************************************/
/*******************************************************************/ 
abstract class Pawn
{
    public final static int SMALL_BOARD_SIZE = 3;
    
    protected final int     _id;
    protected List<int[][]> listOfPawnConfigurations;
    
    protected Position      position;
    
    protected int           numberOfOccupiedFields;
    
    public Pawn(int id)
    {
        _id = id;
        Init();
    }
    
    private void Init()
    {
        listOfPawnConfigurations = new ArrayList<>();
        listOfPawnConfigurations.add(new int[SMALL_BOARD_SIZE][SMALL_BOARD_SIZE]);
        
        position =  new Position();
        
        // sprawdzić czy ma sens .... w klasie position jest -1
        position.x = position.y = -1;   // początkowe przypisanie pozycji 

        numberOfOccupiedFields = 0;
        InitRotateAndFlipPawnPosibilities(_id);
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
    
    public List<int[][]> GetListOfPawnConfigurations()
    {
        return this.listOfPawnConfigurations;
    }

    abstract public void InitRotateAndFlipPawnPosibilities(int id);
    
    public int GetID()
    {
        return _id;
    }
    
    public int GetNumberOfOccupiedFields()
    {
        return numberOfOccupiedFields;
    }
}


/*******************************************************************/
/*******************************************************************/ 
class DotShapePawn extends Pawn
{
    public DotShapePawn(int id)
    {
        super(id+1);
        numberOfOccupiedFields = 1;
    }
    
    @Override
    public void InitRotateAndFlipPawnPosibilities(int id)
    {
        // wprowadzenie początkowych danych, dot shape
        listOfPawnConfigurations.get(position.conf)[1][1] = id;
        
        // tutaj nie jest potrzebne obracaanie
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
    public LShapePawn(int id)
    {
        super(id);
        numberOfOccupiedFields = 4;
    }
    
    @Override
    public void InitRotateAndFlipPawnPosibilities(int id) 
    {
        // wprowadzenie początkowych danych, L shape
        listOfPawnConfigurations.get(position.conf)[0][1]= id;
        listOfPawnConfigurations.get(position.conf)[1][1]= id;
        listOfPawnConfigurations.get(position.conf)[2][1]= id;
        listOfPawnConfigurations.get(position.conf)[2][0]= id;
        
        
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