import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "Board")
public class Board implements Serializable {
    private int rows = 0;
    private int columns = 0;
    private int board[][];

    @XmlAttribute(name = "rows")
    public void setRows(int rows) { this.rows = rows; }

    @XmlAttribute(name = "columns")
    public void setCols(int columns) { this.columns = columns; }

    public Board(){}

    public static Board cloneBoard(Board b){
        Board newBoard = new Board();
        newBoard.setRows(b.getRows());
        newBoard.setCols(b.getCols());
        newBoard.board = new int[b.getRows()][b.getCols()];
        for(int i = 0; i < b.getRows(); i++){
            for(int j = 0; j < b.getCols(); j++){
                int value = b.getValue(i, j);
                newBoard.board[i][j] = value;
            }
        }
        return newBoard;
    }

    public void copyBoard(Board copy){
        for(int i = 0; i < rows; i++){
            for(int  j = 0; j < columns; j++){
                board[i][j] = copy.getValue(i,j);
            }
        }
    }

    public Board(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        initBoard(-1);
    }

    public void initBoard(int value){
        this.board = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = value;
            }
        }
    }

    public int getRows(){return rows;}

    public int getCols(){return columns;}

    public int getSize(){return columns * rows;}

    public int getValue(int i, int j){ return board[i][j];}

    //if success return 0
    public void setValue(int i, int j, int value){
        board[i][j] = value;
        setPossibleChoice(i, j);
    }

    public void setInitialPositions(List<Position> list, int value){
        int i = 0, j = 0;
        for(Position position: list){
            i = position.getRow();
            j = position.getColumn();
            board[i-1][j-1] = value;
            setPossibleChoice(i-1,j-1);
        }
    }

    private void setPossibleChoice(int i, int j){
        if( i-1 >= 0 && j-1 >= 0 && board[i-1][j-1] == -1 )
            board[i-1][j-1] = 0;
        if( j-1 >= 0 && board[i][j-1] == -1)
            board[i][j-1] = 0;
        if(i+1 < rows && j-1 >= 0 && board[i+1][j-1] == -1)
            board[i+1][j-1] = 0;
        if(i-1 >= 0 && board[i-1][j] == -1)
            board[i-1][j] = 0;
        if(i+1 < rows && board[i+1][j] == -1)
            board[i+1][j] = 0;
        if(i-1 >= 0 && j+1 < columns && board[i-1][j+1] == -1)
            board[i-1][j+1] = 0;
        if(j+1 < columns && board[i][j+1] == -1)
            board[i][j+1] = 0;
        if(i+1 < rows && j+1 < columns && board[i+1][j+1] == -1)
            board[i+1][j+1] = 0;
    }

    public void updateBoard(int row, int col, Player currentPlayer) {
        int value = currentPlayer.getId();
        int reversal = 0;
        reversal += updateRow(row, col, value);
        reversal += updateCol(row, col, value);
        reversal += updateMainDiagonal(row, col, value);
        reversal += updateSecondDiagonal(row,col,value);
        currentPlayer.updateReversal(reversal);
    }

    private int updateCol(int row, int col, int value){
        int i = row - 1, j = col - 1;
        int reversal = 0;
        if(i > 0 && discToReverse(i - 1, j, value) ) {
            for (i = i - 1; i >= 0; i--) {
                if(discToReverse(i, j, value))
                    continue;
                if(board[i][j] == value){
                    int k = i + 1;
                    for(; k < row - 1; k++){
                        setValue(k,j,value);
                        reversal++;
                    }
                }
                break;
            }
        }
        i = row - 1;
        if(i < rows - 1 && discToReverse(i + 1,j,value ))
        for (i = i + 1; i < rows; i++){
            if(discToReverse(i, j,value))
                continue;
            if(board[i][j] == value) {
                int k = i - 1;
                for (; k > row - 1; k--) {
                    setValue(k, j, value);
                    reversal++;
                }
            }
            break;
        }
        return  reversal;
    }

    private int updateRow(int row, int col, int value){
        int i = row - 1, j = col - 1;
        int reversal = 0;
        if(j > 0 && discToReverse(i , j - 1, value) ) {
            for(j = j - 1; j >= 0; j --){
                if(discToReverse(i, j, value))
                    continue;
                if(board[i][j] == value){
                    int k = j + 1;
                    for(; k < col - 1; k++ ) {
                        setValue(i, k, value);
                        reversal++;
                    }
                }
                break;
            }
        }

        j = col - 1;
        if( j < columns - 1 && discToReverse(i , j + 1, value)){
            for(j = j + 1; j < columns ; j++){
                if(discToReverse(i, j, value))
                    continue;
                if(board[i][j] == value){
                    int k = j - 1;
                    for(; k > col - 1; k--){
                        setValue(i, k, value);
                        reversal++;
                    }
                }
                break;
            }
        }
        return  reversal;
    }

    private int updateMainDiagonal(int row, int col, int value){
        int i = row - 1, j = col - 1;
        int reversal = 0;
        if(i < rows - 1 && j > 0 && discToReverse(i + 1, j - 1, value) ) {
            for(i = i + 1, j = j - 1; i < rows && j >= 0; i++, j--){
                if(discToReverse(i, j, value))
                    continue;
                if(board[i][j] == value) {
                    int s = i - 1, t = j + 1;
                    for(; s > row - 1 && t < col - 1; s--, t++){
                        setValue(s, t, value);
                        reversal++;
                    }
                }
                break;
            }
        }
        i = row - 1;
        j = col - 1;
        if(i > 0 && j < columns - 1 && discToReverse(i - 1, j + 1, value) ) {
            for(i = i - 1, j = j + 1; i >= 0 && j < columns; i--, j++){
                if(discToReverse(i, j, value))
                    continue;
                if(board[i][j] == value) {
                    int s = i + 1, t = j - 1;
                    for(;s < row -1 && t > col - 1 ; s++, t--){
                        setValue(s, t, value);
                        reversal++;
                    }
                }
                break;
            }
        }
        return  reversal;
    }

    private int updateSecondDiagonal(int row, int col, int value){
        int i = row - 1, j = col - 1;
        int reversal = 0;
        if(i > 0 && j > 0 && discToReverse(i - 1, j - 1, value) ) {
            for(i = i - 1, j = j - 1; i >= 0 && j >= 0; i--, j--){
                if(discToReverse(i, j, value))
                    continue;
                if(board[i][j] == value) {
                    int s = i + 1, t = j + 1;
                    for(; s < row - 1 && t < col - 1; s++, t++){
                        setValue(s, t, value);
                        reversal++;
                    }
                }
                break;
            }
        }
        i = row - 1;
        j = col - 1;
        if(i < rows - 1 && j < columns - 1 && discToReverse(i + 1, j + 1, value) ) {
            for(i = i + 1, j = j + 1; i < rows && j < columns ; i++, j++){
                if(discToReverse(i, j, value))
                    continue;
                if(board[i][j] == value) {
                    int s = i - 1, t = j - 1;
                    for(;s > row -1 && t > col - 1 ; s--, t--){
                        setValue(s, t, value);
                        reversal++;
                    }
                }
                break;
            }
        }
        return  reversal;
    }

    private boolean discToReverse(int row, int col, int value){
        if(board[row][col] != -1 && board[row][col] != 0 && board[row][col] != value)
            return true;
        else return false;
    }

    public int getNumOfDiscs(int value){
        int sum = 0;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(board[i][j] == value)
                    sum++;
            }
        }
        return sum;
    }

    public boolean isFull(){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(board[i][j] == 0 || board[i][j] == -1)
                    return false;
            }
        }
        return  true;
    }

    void printBoard(){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                System.out.print(" " + board[i][j]);
            }
            System.out.println();
        }
    }
}