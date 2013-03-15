import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Michal Franczyk
 * @date 13.03.2013
 */

/**
 * Klasza gracza
 */
public class Player 
{
    public static final int NUMBER_OF_L_SHAPE_FLIP_POSIBILITIES     = 2;    // ilość możliwości "odbicia" L-kształtnego pionka
    public static final int NUMBER_OF_L_SHAPE_ROTATE_POSIBILITIES   = 4;    // ilość możliwości obrotu pionka
    
    public static final int NUMBER_OF_DOT_SHAPE_ROTATE_POSIBILITIES = 0;    // analogicznie jak wyżej
    public static final int NUMBER_OF_DOT_SHAPE_FLIP_POSIBILITIES   = 0;    // jak wyżej
    
    
    private List<Pawn>      pawnsList;      // lista pionków przypadająca na gracza
    private int             currentPawn;    // obecnie ruszany pionek, 0 -L, 1 - .
    private boolean         moved;          // czy udało się wykonać ruch
    
    final public int        _id;            // id gracza
    
    /**
     * publiczny konstruktor
     * inicjalizuje gracza
     * @param id - id gracza
     */
    public Player(int id)
    {
        pawnsList   = new ArrayList<>();

        _id         = id;
        currentPawn = 0;
        moved       = false;

        // dodawanie pionków
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
    
    /**
     * funkcja dokonijąca ruchu gracza
     */
    public void go()
    {
        currentPawn = 0;        // zerowanie pionka w użyciu
        
        moved       = false;    // czy udało się ruszyć?
        
        // dla dużego pionka, L
        UpPawnFromBoard(pawnsList.get(currentPawn));            // podnoszenie pionka
        if( SearchPlaceForPawn(pawnsList.get(currentPawn)) )    // jeśli znalazłem miejsce dla L to ok
        {
            moved = true;       // udało się poruszyć
            DownPawnOnBoard(pawnsList.get(currentPawn));        // położenie pionka 
        }
        
        ++currentPawn;      // następny pionek
        
        // dla małego pionka .
        if(moved == true)  // jeśli udało się poruszyć
        { 
            // działanie - analogicznie jak dla dużego
            UpPawnFromBoard(pawnsList.get(currentPawn));        
            SearchPlaceForPawn(pawnsList.get(currentPawn));
            DownPawnOnBoard(pawnsList.get(currentPawn));
            
            
            ++Game.number_of_movements; // zwiększnie ilości wykonanych ruchów
        }
        
        if(Game.TEST)
            Game.PrintBoard();
    }
    
    /**
     * publiczna
     * Szukanie miejsca dla pionka
     * Sposób postępowania:
     * 1. znalezienie możliwych miejsc do położenia
     * 2. losowanie miejsca
     * 3. wybór miejsca
     * 4. aktualizacja position
     * 5. jeśli brak powodzenia - revert changes, cofnij zmiany
     * @param p pionek dla którego szukam miejsca
     * @return true - znalazłem miejsce, false - nie znalazłem
     */
    public boolean SearchPlaceForPawn(Pawn p)
    {
        // kopie dla szybszego dostępu do danych
        int goodPositions               = 0,                    // ilość pól pasujących do kształtu pionka (względem zadanej pozycji)
            tmpX                        = p.GetPosition().x,    // kopia pozycji pionka, os X
            tmpY                        = p.GetPosition().y,    // kopia pozycji pionka, os Y
            tmpC                        = p.GetPosition().conf, // kopia numeru ułożenia pionka z 8 możiwych sposobów dla dużego L, dla małego to 0
            tmpNumberOfOccupiedFields   = p.GetNumberOfOccupiedFields(), // kopia ilości pół potrzebnych do zbudowania pionka
            tempConfiguration[][];          // kopia ułożenia pionka
        
        List<Position> goodMove = new ArrayList<>();    // lista dobrych pozycji pionka
        
        for(int c = 0; c < p.GetAmoutOfRotatePosibilities(); ++c)   // pętla po wszystkich możliwych ułożeniach pionka = konfiguracjach
        {
            tempConfiguration = p.GetListOfPawnConfigurations().get(c); // przypisnie obecnej konfiguracji
            for(int xpos = 0; xpos < Game.BOARD_SIZE; ++xpos)   // pętla po osi x planszy gry
            {
                for(int ypos = 0; ypos < Game.BOARD_SIZE; ++ypos) // pętla po osi y planszy gry
                {
                    goodPositions = 0;  // zerowanie dla każdej nowej badanej pozycji

                    if( ( (xpos == tmpX) && (ypos == tmpY) ) && (c == tmpC) )   // omijanie istaniejącej pozycji pionka na planszy
                        continue;   // jeśli chce położyć pionka w tym miejscu, zbadaj inne miejsce, konfigurację

                    // teraz główna częśc algorytmu:
                    // Założenia: środek macierzy 3x3 jest w pkt 1,1
                    //          : środek macierzy jest punktem "odwołania" względem którego będzie sprawdzana możliwość położenia pionka
                    //          : w planszy oraz w macierzy konfiguracji: 0 - puste, numer -> zajęte pole
                    //
                    // Zmienne  : x, y - zmienne określające obecnie sprawdzany punkt w macierzach konfiguracji (możliwości ułożenia pionków), macierz ma wielkość 3x3
                    //          : i, j - zmienne określające położenie na osi x, y w macierzy 4,4 -> względem obecnie sprawdzanego punktu
                    //
                    // Rozw     : 
                    // 1. dla punktu (x,y), konfiguracji c sprawdzaj punkty w planszy (macierzy)
                    //      używając indeksów i, j ( punkty są sprawdzane pod kątem posiadanej wartości )
                    // 2. jeśli udało się położyć całego pionka (goodPositions jest równe ilości potrzebnych pól do zbudowania pionka)
                    //    zasada spradzania:
                    //          2.1 wraz z indeksem i, j, zwiększa się indeks x, y (po tablicy 3x3)
                    //          2.2 jeśli w macierzy konfiguracji pionka znajdzie się wartość różna od zera przejdz do 2.3
                    //          2.3 jeśli w punkcie i,j na planszy jest 0 to zwiększ goodPosiotions 0 jeden;
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
            
            // losowane pozycji ułożenia z pośród wszystkich możliwych
            // zatwierdzenie pozycji
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
    
    /**
     * Ppubliczny
     * podnoszenie pionka z planszy
     * @param p pionek
     */
    public void UpPawnFromBoard(Pawn p)
    {
        // kopie dla szybszego dostępu do danych
        int xpos                    = p.GetPosition().x,    // pozycja pionka, os X
            ypos                    = p.GetPosition().y,    // pozycja pionka, os Y
            tempConfiguration[][]   = p.GetListOfPawnConfigurations().get(p.GetPosition().conf), // kopia bierzącego usawienia pionka
            tempID                  = p.GetID(); // kopia ID gracza
 
        // i, j -> położenie na planszy 4x4
        // x, y -> położenie na macierzy konfiguracji pionka 3x3
        // w miejsce pionka jest wpisywane 0
        for(int i = xpos - 1, x = 0; i < xpos + 2 && x < Pawn.SMALL_BOARD_SIZE; ++i, ++x)
            if(i>-1 && i<4)
                for(int j = ypos - 1, y=0; j < ypos + 2 && y < Pawn.SMALL_BOARD_SIZE; ++j, ++y)
                    if(j>-1 && j<4)
                        if( tempConfiguration[x][y] == tempID)
                            Game.board[i][j] =  0;
    }
    
    /**
     * publiczna
     * kładzenie pionka na planszy
     * @param p pionek
     */
    public static void DownPawnOnBoard(Pawn p)
    {
        // kopie dla szybszego dostępu do danych
        int xpos                    = p.GetPosition().x,    // pozycja pionka, os X
            ypos                    = p.GetPosition().y,    // pozycja pionka, os Y
            tempConfiguration[][]   = p.GetListOfPawnConfigurations().get(p.GetPosition().conf); // kopia bierzącego usawienia pionka
        
        
        // i, j -> położenie na planszy 4x4
        // x, y -> położenie na macierzy konfiguracji pionka 3x3
        // tam gdzie (x, y) != wpisz wartość spod (x,y) do planszy w punkcie i, j
        for(int i = xpos - 1, x = 0; i < xpos + 2 && x < Pawn.SMALL_BOARD_SIZE; ++i, ++x)
            if(i>-1 && i<4)
                for(int j = ypos - 1, y=0; j < ypos + 2 && y < Pawn.SMALL_BOARD_SIZE; ++j, ++y)
                    if(j>-1 && j<4)
                        if( tempConfiguration[x][y] != 0)
                            Game.board[i][j] =  tempConfiguration[x][y];
    }
    
    /**
     * pobranie listy pionków gracza
     * @return lista pionków
     */
    public List<Pawn> GetPawns()
    {
        return pawnsList;
    }
}
/*******************************************************************/


/*******************************************************************/
/*******************************************************************/ 
/**
 * Klasa Pozycji
 */
class Position
{
    // x, y współrzędne
    // conf konfiguracja pionka
    public int x, y, conf;

    // konstruktory służące do inicjalizacji obiektu klasy Pozycja
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
    
    /**
     * metoda umożliwiająca łatwy wypis obiektu
     * @return wypis obiektu
     */
    @Override
    public String toString() 
    {
        return("["+x+" "+y+" "+conf+"]");
    }
}


/*******************************************************************/
/*******************************************************************/ 
/**
 * Klasa pionka
 */
abstract class Pawn
{
    public final static int SMALL_BOARD_SIZE = 3;       // rozmiar tablicy
    
    protected final int     fieldValue;                 // wartość wpisywana w miejsce w którym pionek będzie znajdować się pionek
    protected List<int[][]> listOfPawnConfigurations;   // lista konfiguracji, macierzy, o romiarze SMALL_BOARD_SIZE, przechowuje wszystkie możiwe ułożenia pionka, dla L - 8, dla . - 1
    
    protected Position      position;                   // pozycja pionka wraz z obecnie używaną konfiguracją
    
    protected int           numberOfOccupiedFields;     // ilość zajmowanych miejsc przez pionek
    
    /**
     * konsktuktor
     * @param fieldValue wartość wpisywana w miejsce w którym pionek będzie znajdować się pionek
     */
    public Pawn(int fieldValue)
    {
        this.fieldValue = fieldValue;
        Init(); // inicjalizacja pionka
    }
    
    /**
     * Inicjalizacja pionka
     */
    private void Init()
    {
        listOfPawnConfigurations = new ArrayList<>();
        listOfPawnConfigurations.add(new int[SMALL_BOARD_SIZE][SMALL_BOARD_SIZE]);
        
        position =  new Position();
        
        // sprawdzić czy ma sens .... w klasie position jest -1
        position.x = position.y = -1;   // początkowe przypisanie pozycji 

        numberOfOccupiedFields = 0;
        InitRotateAndFlipPawnPosibilities(fieldValue);
    }
    
    /**
     * Do testowego wypisu wszystkich konfiguracji pionka
     */
    public void PrintAllConfigurations()
    {
        System.out.println("----- "+fieldValue+" -----");
        for(int i=0; i < listOfPawnConfigurations.size(); ++i)
        {
            System.out.println("Conf" + i + "= ");
            Print(listOfPawnConfigurations.get(i));
            System.out.println();
        }
        System.out.println("---------------");
    }
    
    /**
     * Do testowego wypisu konkretnej konfiguracji pionka
     */
    public void Print(int[][] matrixToPrint)
    {
        for ( int[] row : matrixToPrint ) 
        {
            System.out.println(Arrays.toString(row));
        }
    }
    
    /**
     * obraca zadaną macierz konfiguracji zgodnie z ruchem wskazówek zegara
     * @param boardToRotate marcierz do obrotu, macierz konfiguracji
     * @return macierz po obrocie
     */
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
    
    /**
     * odbicie horyzontalne macierzy konfiguracji
     * @param boardToFlip macierz którą będziemy odbijać
     * @return macierz po odbicu
     */
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
    
        /**
     * odbicie macierzy konfiguracji względem pionowej linii przechodzącej przez środek
     * @param boardToFlip macierz którą będziemy odbijać
     * @return macierz po odbicu
     */
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
    
    /**
     * wypełenienie listy konfiguracjio pionka
     * @param start rozpoczęcie pętli
     * @param stop zakończenie pętli
     */
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
    
    /**
     * 
     * @return ilość dostępnych konfiguracjio pionka
     */
    public int GetAmoutOfRotatePosibilities()
    {
        return this.listOfPawnConfigurations.size();
    }
    
    /**
     * Ustawianie pozycji
     * @param pos pozycja do ustawienia
     */
    public void SetPosition(Position pos)
    {
        this.position = pos;
    }
    
    /**
     * 
     * @return pobieranie poycji
     */
    public Position GetPosition()
    {
        return this.position;
    }
    
    /**
     * Pobranie konkretnej konfiguracji
     * @param index numer konfiguracji
     * @return konfigurację, macierz, pod index
     */
    public int[][] GetPawnConfiguration(int index)
    {
        return this.listOfPawnConfigurations.get(index);
    }
    
    /**
     * @return lista wszystkich dostępnych konfiguracji
     */
    public List<int[][]> GetListOfPawnConfigurations()
    {
        return this.listOfPawnConfigurations; 
    }

    /**
     * pobranie id
     * @return id
     */
    public int GetID()
    {
        return fieldValue;
    }
    
    /**
     * Pobranie 
     * @return ilość zajmowanych miejsc przez pionek
     */
    public int GetNumberOfOccupiedFields()
    {
        return numberOfOccupiedFields;
    }
    
    /**
     * funckja inicjalizująca plansze konfiguracji pionków
     * @param id 
     */
    abstract public void InitRotateAndFlipPawnPosibilities(int fieldValue);
}


/*******************************************************************/
/*******************************************************************/ 
/**
 * Klasa małego pionka
 */
class DotShapePawn extends Pawn
{
    public DotShapePawn(int fieldValue)
    {
        super(fieldValue+1);
        numberOfOccupiedFields = 1;
    }
    
    @Override
    public void InitRotateAndFlipPawnPosibilities(int fieldValue)
    {
        // wprowadzenie początkowych danych, dot shape
        listOfPawnConfigurations.get(position.conf)[1][1] = fieldValue;
        
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
/**
 * Klasa dużego pionka
 */
class LShapePawn extends Pawn
{
    public LShapePawn(int fieldValue)
    {
        super(fieldValue);
        numberOfOccupiedFields = 4;
    }
    
    @Override
    public void InitRotateAndFlipPawnPosibilities(int fieldValue) 
    {
        // wprowadzenie początkowych danych, L shape
        listOfPawnConfigurations.get(position.conf)[0][1]= fieldValue;
        listOfPawnConfigurations.get(position.conf)[1][1]= fieldValue;
        listOfPawnConfigurations.get(position.conf)[2][1]= fieldValue;
        listOfPawnConfigurations.get(position.conf)[2][0]= fieldValue;
        
        
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