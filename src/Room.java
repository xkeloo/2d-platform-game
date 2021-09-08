import java.util.ArrayList;

public class Room implements Comparable<Room>{
    public ArrayList<Coord> tiles;
    public ArrayList<Coord> edgeTiles;
    public ArrayList<Room> connectedRooms;
    public Integer roomSize;
    public boolean isAccessibleFromMainRoom;
    public boolean isMainRoom;

    public Room() {

    }

    public Room(ArrayList<Coord> roomTiles, int[][] map) {
       tiles = roomTiles;
       roomSize = tiles.size();
       connectedRooms = new ArrayList<Room>();
       edgeTiles = new ArrayList<Coord>();
       for(Coord tile : tiles) {
           for(int x = tile.x - 1; x <= tile.x + 1; x++ ) {
               for(int y = tile.y - 1; y <= tile.y + 1; y++ ) {
                   if(x == tile.x || y == tile.y) {
                       if(map[x][y] == 1) {
                           edgeTiles.add(tile);
                       }
                   }
               }
           }
       }
    }
    public void setAccesibleFormMainRoom() {
        if(!isAccessibleFromMainRoom) {
            isAccessibleFromMainRoom = true;
            for(Room connectedRoom : connectedRooms) {
                connectedRoom.setAccesibleFormMainRoom();
            }
        }
    }

    public static void ConnectRooms(Room roomA, Room roomB) {
        if(roomA.isAccessibleFromMainRoom) {
            roomB.setAccesibleFormMainRoom();
        }
        else if(roomB.isAccessibleFromMainRoom) {
            roomA.setAccesibleFormMainRoom();
        }
        roomA.connectedRooms.add(roomB);
        roomB.connectedRooms.add(roomA);
    }

    public boolean isConnected(Room otherRoom) {
        return connectedRooms.contains(otherRoom);
    }


    @Override
    public int compareTo(Room o) {
        return o.roomSize.compareTo(roomSize);
    }
}
