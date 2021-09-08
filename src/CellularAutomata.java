import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * klasa algorymtu tworzącego jaskinie
 */
public class CellularAutomata {



    /**
     * główna metoda algorytmu, wywołująca inne metody klasy, kolejno:
     * - losowo wypełnia mapę blokami,
     * - następnie wykonuje iteracje algorytmu automatu komórkowego
     *
     * @param map tablica przechowująca mapę bloków
     * @param chance szansa na pojawienie się 'sciany' - bloku o id 1
     * @param deathLimit liczba otaczajacych bloków, poniżej której blok ściany i zmienia się w powietrze
     * @param birthLimit liczba otaczajacych bloków, powyżej której blok powyżej i zmienia się w ścianę
     * @param loops liczba iteracji algorytmu
     * @return zaktualizwana mapa
     */
    public static int[][] generate(int[][] map, double chance, int deathLimit, int birthLimit, int loops)
    {
        int[][] newMap = randomize(map, chance);
      //  newMap = horizontalBlanking(newMap, birthLimit);
        for (int i = 0; i < loops; i++)
        {
            newMap = simulateStep(map, deathLimit, birthLimit);
        }
        newMap = createBorders(newMap);
        newMap = ProcessMap(newMap);

        return newMap;

    }



    public static int[][] ProcessMap(int[][] map) {
        ArrayList<ArrayList<Coord>> wallRegions = GetRegions(map,1);

        int wallThresholdSize = 50;
        for(ArrayList<Coord> wallRegion: wallRegions) {
            if(wallRegion.size() < wallThresholdSize) {
                for(Coord tile : wallRegion) {
                    map[tile.x][tile.y] = 0;
                }
            }
        }

        ArrayList<ArrayList<Coord>> roomRegions = GetRegions(map,0);

        int roomThresholdSize = 50;
        ArrayList<Room> survivingRooms = new ArrayList<Room>();

        for(ArrayList<Coord> roomRegion: roomRegions) {
            if(roomRegion.size() < roomThresholdSize) {
                for(Coord tile : roomRegion) {
                    map[tile.x][tile.y] = 1;
                }
            }
            else {
                survivingRooms.add(new Room(roomRegion, map));
            }
        }
        Collections.sort(survivingRooms);
        survivingRooms.get(0).isMainRoom = true;
        survivingRooms.get(0).isAccessibleFromMainRoom = true;
        for(Room r : survivingRooms) {
            System.out.println(r.roomSize);
        }


        ConnectClosestRooms(map, survivingRooms, false);

        return map;
    }

    public static int[][] ConnectClosestRooms(int[][] map, ArrayList<Room> allRooms, boolean forceAccessibilityFormMainRoom) {

        ArrayList<Room> roomListA = new ArrayList<Room>();
        ArrayList<Room> roomListB = new ArrayList<Room>();

        if(forceAccessibilityFormMainRoom) {
            for(Room room : allRooms) {
                if(room.isAccessibleFromMainRoom) {
                    roomListB.add(room);
                }
                else
                    roomListA.add(room);
            }
        } else {
            roomListA = allRooms;
            roomListB = allRooms;
        }


        int bestDistance = 0;
        Coord bestTileA = new Coord();
        Coord bestTileB = new Coord();
        Room bestRoomA = new Room();
        Room bestRoomB = new Room();
        boolean possibleConnectionFound = false;

        for(Room roomA : roomListA) {
            if(!forceAccessibilityFormMainRoom) {
                possibleConnectionFound = false;
                if(roomA.connectedRooms.size() > 0) {
                    continue;
                }
            }

            for(Room roomB : roomListB) {
                if(roomA == roomB || roomA.isConnected(roomB)) {
                    continue;
                }

                for(int tileIndexA = 0; tileIndexA < roomA.edgeTiles.size(); tileIndexA++) {
                    for(int tileIndexB = 0; tileIndexB < roomB.edgeTiles.size(); tileIndexB++) {
                        Coord tileA = roomA.edgeTiles.get(tileIndexA);
                        Coord tileB = roomB.edgeTiles.get(tileIndexB);
                        int distanceBetweenRooms = (int) (Math.pow(tileA.x -tileB.x, 2) + Math.pow(tileA.y -tileB.y, 2));

                        if(distanceBetweenRooms < bestDistance || !possibleConnectionFound) {
                            bestDistance = distanceBetweenRooms;
                            possibleConnectionFound = true;
                            bestTileA = tileA;
                            bestTileB = tileB;
                            bestRoomA = roomA;
                            bestRoomB = roomB;
                        }
                    }
                }
            }
            if(possibleConnectionFound  && !forceAccessibilityFormMainRoom) {
                createPassage(map, bestRoomA, bestRoomB, bestTileA, bestTileB);
            }

        }

        if(possibleConnectionFound && forceAccessibilityFormMainRoom) {
            createPassage(map, bestRoomA, bestRoomB, bestTileA, bestTileB);
            ConnectClosestRooms(map, allRooms, true);
        }

        if(!forceAccessibilityFormMainRoom) {
            ConnectClosestRooms(map, allRooms, true);
        }
        return map;
    }

    public static int[][] createPassage(int[][] map, Room roomA, Room roomB, Coord tileA, Coord tileB) {
        Room.ConnectRooms(roomA, roomB);

        System.out.println("A: " + tileA.x + " " + tileA.y + "B: " + tileB.x + " " + tileB.y);

        if(tileA.x >= tileB.x && tileA.y >= tileB.y) {
            for(int x = tileB.x; x <= tileA.x; x++) {
                for(int y = tileB.y - 1; y <= tileB.y + 1; y++) {
                    map[x][y] = 0;
                }
            }
            for(int y = tileB.y; y <= tileA.y; y++) {
                for(int x = tileA.x - 1; x <= tileA.x + 1; x++) {
                    map[x][y] = 0;
                }
            }
        } else if(tileA.x >= tileB.x && tileA.y < tileB.y) {
            for(int x = tileB.x; x <= tileA.x; x++) {
                for(int y = tileB.y - 1; y <= tileB.y + 1; y++) {
                    map[x][y] = 0;
                }
            }
            for(int y = tileA.y; y <= tileB.y; y++) {
                for(int x = tileA.x - 1; x <= tileA.x + 1; x++) {
                    map[x][y] = 0;
                }
            }
        } else if(tileA.x < tileB.x && tileA.y < tileB.y) {
            for(int x = tileA.x; x <= tileB.x; x++) {
                for(int y = tileA.y - 1; y <= tileA.y + 1; y++) {
                    map[x][y] = 0;
                }
            }
            for(int y = tileA.y; y <= tileB.y; y++) {
                for(int x = tileB.x - 1; x <= tileB.x + 1; x++) {
                    map[x][y] = 0;
                }
            }
        } else if(tileA.x < tileB.x && tileA.y >= tileB.y) {
            for(int x = tileA.x; x <= tileB.x; x++) {
                for(int y = tileA.y - 1; y <= tileA.y + 1; y++) {
                    map[x][y] = 0;
                }
            }
            for(int y = tileB.y; y <= tileA.y; y++) {
                for(int x = tileB.x - 1; x <= tileB.x + 1; x++) {
                    map[x][y] = 0;
                }
            }
        }







        return map;
    }

    public static ArrayList<ArrayList<Coord>> GetRegions(int[][] map, int tileType) {
        ArrayList<ArrayList<Coord>> regions = new ArrayList<ArrayList<Coord>>();
        int[][] mapFlags = new int[Settings.mapWidth][Settings.mapHeight];

        for(int x = 0; x < Settings.mapWidth; x++) {
            for (int y = 0; y < Settings.mapHeight; y++) {
                if(mapFlags[x][y] == 0 && map[x][y] == tileType) {
                    ArrayList<Coord> newRegion = GetRegionTiles(map, x, y);
                    regions.add(newRegion);

                    for(Coord tile:newRegion) {
                        mapFlags[tile.x][tile.y] = 1;
                    }
                }
            }
        }
        return regions;
    }

   public static ArrayList<Coord> GetRegionTiles(int[][]map, int startX, int startY) {
        ArrayList<Coord> tiles = new ArrayList<Coord>();

        int[][] mapFlags = new int[Settings.mapWidth][Settings.mapHeight];
        int tileType = map[startX][startY];

        Queue<Coord> queue = new LinkedList<Coord>();
        queue.add(new Coord(startX, startY));
        mapFlags[startX][startY] = 1;

        while(queue.size() > 0) {
            Coord tile = queue.remove();
            tiles.add(tile);

            for(int x = tile.x - 1; x <= tile.x +1; x++) {
                for(int y = tile.y - 1; y <= tile.y +1; y++) {
                    if(isInMapRange(x, y) && (y == tile.y || x == tile.x)) {
                        if(mapFlags[x][y] == 0 && map[x][y] == tileType) {
                            mapFlags[x][y] = 1;
                            queue.add(new Coord(x, y));
                        }
                    }
                }
            }
        }
        return tiles;
    }

    public static boolean isInMapRange(int x, int y) {
        return x >= 0 && x < Settings.mapWidth && y >= 0 && y < Settings.mapHeight;
    }

    public static int[][] createBorders(int[][] map) {
        for(int x = 0; x < map.length; x++) {
            map[x][0] = 1;
            map[x][map[0].length-1] = 1;
        }
        for(int y = 0; y < map[0].length; y++) {
            map[0][y] = 1;
            map[map.length-1][y] = 1;
        }
        return map;
    }


    /**
     * aktualizuje mapę, według algorytmu automatu komórkowego
     *
     * @param map tablica przechowująca mapę bloków
     * @param deathLimit liczba otaczajacych bloków, poniżej której blok ściany i zmienia się w powietrze
     * @param birthLimit liczba otaczajacych bloków, powyżej której blok powyżej i zmienia się w ścianę
     * @return
     */
    public static int[][] simulateStep(int[][] map, int deathLimit, int birthLimit)
    {
        int[][] newMap = map;

        int counter = 0;
        for(int x = 0; x < map.length; x++) {
            for(int y = 0; y < map[x].length; y++)
            {
                counter = 0;

                for(int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j ++)
                    {
                        int n_x = x + i;
                        int n_y = y + j;

                        if(i == 0 && j == 0) {}
                        else if (n_x < 0 || n_y < 0 || n_x >= map.length || n_y >= map[0].length)
                            counter++;
                        else if(map[n_x][n_y] == 1)
                            counter++;
                    }
                }


                if(map[x][y] == 1)
                    newMap[x][y] = (counter < deathLimit) ? 0 : 1;
                else if(map[x][y] == 0)
                    newMap[x][y] = (counter > birthLimit) ? 1 : 0;
            }
        }
        return newMap;
    }

    /**
     * losowo uzupełnia mapę blokami w stosunku ściana/powietrze równym parametrowi chance
     *
     * @param map tablica przechowująca mapę bloków
     * @param chance szansa na pojawienie się 'sciany' - bloku o id 1
     * @return
     */
    public static int[][] randomize(int[][] map, double chance)
    {
        for(int x = 0; x < map.length; x++)
        {
            for(int y = 0; y < map[0].length; y++)
            {
                if(Math.random() <= chance)
                    map[x][y] = 1;
                else
                    map[x][y] = 0;
            }
        }
        return map;
    }


}
