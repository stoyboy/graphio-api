package at.stoyboy.graphioapi;

public abstract class Matrix {
    protected Integer[][] matrix;

    public Integer[][] getMatrix() {
        return this.matrix;
    }

    public void setMatrix(Integer[][] matrix) {
        this.matrix = matrix;
    }

    public void printMatrix() {
        Integer[][] matrix = this.getMatrix();
        int size = matrix.length;
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] == null)
                    str.append("\u221e" + "|");
                else
                    str.append(matrix[i][j]).append("|");
            }
            str.append(i == size - 1 ? "" : "\n");
        }
        System.out.println(str);
    }
}
