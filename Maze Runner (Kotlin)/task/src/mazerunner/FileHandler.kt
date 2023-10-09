package mazerunner

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

object FileHandler {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    private val type = Types.newParameterizedType(MutableList::class.java, MutableList::class.java, Cell::class.java,)
    private val mazeAdapter = moshi.adapter<MutableList<MutableList<Cell>>>(type)

    fun saveMaze(maze: Maze, fileName: String) {
        val file = File(fileName)
        val json = mazeAdapter.toJson(maze.grid)
        println(json)
        println(maze.grid)
        file.writeText(json)
    }

    fun loadMaze(fileName: String) {
        val file = File(fileName)
        if (file.exists()) {
            val text = file.readText().trimIndent()
            if (text.isNotEmpty()) {
                try {
                    val mazeGrid = mazeAdapter.fromJson(text)!!
                    val height = mazeGrid.size
                    val length = mazeGrid[0].size
                    val result = text.replace("[\\[\\]]".toRegex(),"")
                        .split("},")
                        .map { it.split(",").map { it.substringAfter(":").substringBefore("}") } }
                    maze = Maze(height, length)
                    result.forEach {
                        val x = it[0].toInt()
                        val y = it[1].toInt()
                        val wall = it[2].toBoolean()
                        maze!!.grid[x][y] = Cell(x, y, wall)
                    }

                } catch (_: Exception) {
                    println("Cannot load the maze. It has an invalid format")
                }

            }
        } else {
            println("The file $fileName does not exist")
        }
    }

}



