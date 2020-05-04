package server;

import java.util.ArrayList;

public class VierOpEenRij {
    private final ArrayList<ArrayList<Integer>> grid;
    private int w;
    private int h;

    public static void main(String[] args) {
        new VierOpEenRij(10, 25);
    }

    public VierOpEenRij(int w, int h) {
        this.w = w;
        this.h = h;
        grid = new ArrayList<>();
        for (int i = 0; i < this.w; i++) {
            grid.add(new ArrayList<>());
//            for (int j = 0; j < h; j++) {
//                grid.get(i).add(null);
//            }
            System.out.println((i+1)+ ": "+ grid.get(i).size());
        }

    }

    public void move(int player, int w) throws Exception{
        if (grid.get(w).size() < this.h){
            grid.get(w).add(player);
        }else {
            throw new Exception("Invalid move");
        }
    }

    public boolean checkWin(int player){

        // horizontalCheck
        for (int j = 0; j<this.h-3 ; j++ ){
            for (int i = 0; i<this.w; i++){
                if (this.grid.get(i).get(j) == player && this.grid.get(i).get(j + 1) == player && this.grid.get(i).get(j + 2) == player && this.grid.get(i).get(j + 3) == player){
                    return true;
                }
            }
        }
        // verticalCheck
        for (int i = 0; i<this.w-3 ; i++ ){
            for (int j = 0; j<this.h; j++){
                if (this.grid.get(i).get(j) == player && this.grid.get(i + 1).get(j) == player && this.grid.get(i + 2).get(j) == player && this.grid.get(i + 3).get(j) == player){
                    return true;
                }
            }
        }
        // ascendingDiagonalCheck
        for (int i=3; i<this.w; i++){
            for (int j=0; j<this.h-3; j++){
                if (this.grid.get(i).get(j) == player && this.grid.get(i - 1).get(j + 1) == player && this.grid.get(i - 2).get(j + 2) == player && this.grid.get(i - 3).get(j + 3) == player)
                    return true;
            }
        }
        // descendingDiagonalCheck
        for (int i=3; i<this.w; i++){
            for (int j=3; j<this.h; j++){
                if (this.grid.get(i).get(j) == player && this.grid.get(i - 1).get(j - 1) == player && this.grid.get(i - 2).get(j - 2) == player && this.grid.get(i - 3).get(j - 3) == player)
                    return true;
            }
        }
        return false;
    }
}
