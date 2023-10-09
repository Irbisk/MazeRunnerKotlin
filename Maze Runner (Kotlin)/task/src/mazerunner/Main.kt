package mazerunner


fun main() {
    menu()
}

fun menu() {
    while (true) {
        if (maze != null) {
            println("=== Menu ===\n" +
                    "1. Generate a new maze\n" +
                    "2. Load a maze\n" +
                    "3. Save the maze\n" +
                    "4. Display the maze\n" +
                    "5. Find the escape\n" +
                    "0. Exit")

        } else {
            println("=== Menu ===\n" +
                    "1. Generate a new maze\n" +
                    "2. Load a maze\n" +
                    "0. Exit")
        }
        when (readln().toInt()) {
            1 -> {
                println("Enter the size of a new maze")
                val size = readln().toInt()
                val newMaze = Maze(size, size)
                newMaze.generate()
                maze = newMaze
                maze!!.printMaze()
            }
            2 -> {
                val fileName = readln()
                FileHandler.loadMaze(fileName)
            }
            3 -> {
                if (maze != null) {
                    val fileName = readln()
                    FileHandler.saveMaze(maze!!, fileName)
                } else {
                    println("Incorrect option. Please try again")
                }
            }
            4 -> {
                if (maze != null) {
                    maze!!.printMaze()
                } else {
                    println("Incorrect option. Please try again")

                }
            }
            5 -> {
                if (maze != null) {
                    ShortestPath(maze!!.createAdjList()).bfs()
                } else {
                    println("Incorrect option. Please try again")
                }
            }
            0 -> {
                println("Bye!")
                break
            }
            else -> println("Incorrect option. Please try again")

        }
    }
}