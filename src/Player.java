
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michal
 */
public class Player 
{
    private Pawn lShapePawn, dotShapePawn;
    final public int _id;
    
    public Player(int id)
    {
        dotShapePawn    = new DotShapePawn(id);
        lShapePawn      = new LShapePawn(id);
    
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
    //
    public final static int SMALL_BOARD_SIZE = 3;
    
    //
    protected int[][]       _smallBoard;
    protected final int     _id;
    protected List<int[][]> listOfRotatePosibilities;
    
    // konstruktor
    public Pawn(int id)
    {
        _smallBoard = new int[SMALL_BOARD_SIZE][SMALL_BOARD_SIZE];
        listOfRotatePosibilities = new ArrayList<>();
        
        _id = id;

        InitPawnBoardAndRotatePosibilities(id);
    }
    
    //
    public void print()
    {
        for( int[] i : _smallBoard )
        {
            for(int j : i)
                System.out.print(j + "\t");
            System.out.println();
        }
        System.out.println();
    }
    
    abstract public void InitPawnBoardAndRotatePosibilities(int id);
    abstract public int GetAmoutOfRotatePosibilities();

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
        _smallBoard[1][1] = id;
        
        try
        {
            if(listOfRotatePosibilities.size() != 0)
                throw new Exception("[Błąd], możliwości obrotu w dotShape != 0");
        }
        catch(Exception ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public int GetAmoutOfRotatePosibilities() 
    {
        return listOfRotatePosibilities.size();
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
        _smallBoard[0][1]= id;
        _smallBoard[1][1]= id;
        _smallBoard[2][1]= id;
        _smallBoard[2][0]= id;
     }

    @Override
    public int GetAmoutOfRotatePosibilities() 
    {
        return listOfRotatePosibilities.size();
    }
    
}