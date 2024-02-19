package Peasys.PeaResponse;

/**
 * Represents the concept of metadata for a column of an SQL table on the AS/400 server.
 */
public final class ColumnInfo {
    public String columnName;
    public int ordinalPosition;
    public String dataType;
    public int length;
    public int numericScale;
    public String isNullable;
    public String isUpdatable;
    public String longComment;
    public int numericPrecision;

    /**
     * Initialize a new instance of the ColumnInfo class.
     * @param columnName Name of the column.
     * @param ordinalPosition Ordinal position of the column.
     * @param dataType DB2 Type of the data contain in the column.
     * @param length Length of the data contain in the column.
     * @param numericScale Scale of the data contain in the column if numeric type.
     * @param isNullable Y/N depending on the updatability of the field.
     * @param isUpdatable Y/N depending on the nullability of the field.
     * @param longComment Description of the column, maybe be empty.
     * @param numericPrecision Precision of the data contain in the column if numeric type.
     */
    public ColumnInfo(String columnName, int ordinalPosition, String dataType, int length, int numericScale, String isNullable,
                      String isUpdatable, String longComment, int numericPrecision) {
        this.columnName = columnName;
        this.ordinalPosition = ordinalPosition;
        this.dataType = dataType;
        this.length = length;
        this.numericScale = numericScale;
        this.isNullable = isNullable;
        this.isUpdatable = isUpdatable;
        this.longComment = longComment;
        this.numericPrecision = numericPrecision;
    }
}
