import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "Position")
public class Position implements Serializable {
    private int row;
    private int column;

    @XmlAttribute(name = "column")
    public void setCols(int column) {
        this.column = column;
    }

    @XmlAttribute(name = "row")
    public void setRows(int row) {
        this.row = row;
    }

    public Position(){}

    public Position(int i, int j){
        row = i;
        column = j;
    }

    public int getColumn() { return column; }

    public int getRow() { return row; }

    public double getDistance(Position position){
        double x = position.row;
        double y = position.column;
        double distanceX = x-row;
        double distanceY = y-column;
        if(distanceX < 0) distanceX *= -1;
        if(distanceY < 0) distanceY *= -1;
        return Math.min(distanceX,distanceY);
    }

    public int compareTo(Position position){
        if(position.getRow() == row && position.getColumn() == column)
            return 0;
       return -1;
    }

    @Override
    public String toString(){
        return "("+row + "," + column + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Position))
            return false;
        if (obj == this)
            return true;
        return this.getRow() == ((Position) obj).getRow() && this.getColumn() == ((Position)obj).getColumn();
    }

    @Override
    public int hashCode() {
        return 17*row + 11*column/3;
    }
}

