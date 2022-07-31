package cga.exercise.components.Logic

import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.ModelLoader
import org.joml.Math
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f

class CellField:Transformable(){

    private var texDiff = Texture2D("assets/textures/ground_diff.png", true)
    private var texSpec = Texture2D("assets/textures/ground_spec.png", true)

    private var texEmitpink = Texture2D("assets/textures/pink.png", true)
    private var texEmitLightblue = Texture2D("assets/textures/lightblue.png", true)

    private var materialPink = Material(texDiff,texEmitpink,texSpec)
    private var materialBlue = Material(texDiff,texEmitLightblue,texSpec)

    private var cell = loadCellModel()

    var pathIsHidden = false

    class Cell(var material: Material, pos: Vector3f? = null):Transformable()
    {
        var belongsToPath = false
        init {
            if(pos!=null){translate(pos!!)}
        }
    }
    var matrix = mutableListOf<MutableList<Cell?>>()
    var size = Vector2i(14,5)

    init {
        for(x in 0..size.x-1)
        {
            matrix.add(mutableListOf())
            for(y in 0..size.y-1)
            {
                matrix[x].add(null)
            }
        }

        //1-5
        matrix[0][1] = Cell(materialBlue,getCellPosition(0,1))
        matrix[0][2] = Cell(materialBlue,getCellPosition(0,2))
        matrix[0][3] = Cell(materialBlue,getCellPosition(0,3))
        matrix[1][1] = Cell(materialPink,getCellPosition(1,1))
        matrix[1][2] = Cell(materialBlue,getCellPosition(1,2))
        //6-10
        matrix[1][3] = Cell(materialBlue, getCellPosition(1,3))
        matrix[1][4] = Cell(materialBlue,getCellPosition(1,4))
        matrix[2][1] = Cell(materialBlue,getCellPosition(2,1))
        matrix[2][2] = Cell(materialBlue,getCellPosition(2,2))
        matrix[2][3] = Cell(materialBlue,getCellPosition(2,3))
        //11-15
        matrix[2][4] = Cell(materialBlue, getCellPosition(2,4))
        matrix[3][1] = Cell(materialBlue,getCellPosition(3,1))
        matrix[3][2] = Cell(materialBlue,getCellPosition(3,2))
        matrix[3][3] = Cell(materialBlue,getCellPosition(3,3))
        matrix[3][4] = Cell(materialBlue,getCellPosition(3,4))
        //16-20
        matrix[4][1] = Cell(materialBlue, getCellPosition(4,1))
        matrix[4][2] = Cell(materialBlue,getCellPosition(4,2))
        matrix[4][3] = Cell(materialBlue,getCellPosition(4,3))
        matrix[4][4] = Cell(materialBlue,getCellPosition(4,4))
        matrix[5][2] = Cell(materialBlue,getCellPosition(5,2))
        //21-25
        matrix[5][3] = Cell(materialBlue, getCellPosition(5,3))
        matrix[5][4] = Cell(materialBlue,getCellPosition(5,4))
        matrix[6][1] = Cell(materialBlue,getCellPosition(6,1))
        matrix[6][2] = Cell(materialBlue,getCellPosition(6,2))
        matrix[6][3] = Cell(materialBlue,getCellPosition(6,3))
        //26-30
        matrix[6][4] = Cell(materialBlue, getCellPosition(6,4))
        matrix[7][1] = Cell(materialBlue,getCellPosition(7,1))
        matrix[7][2] = Cell(materialBlue,getCellPosition(7,2))
        matrix[7][3] = Cell(materialBlue,getCellPosition(7,3))
        matrix[7][4] = Cell(materialBlue,getCellPosition(7,4))
        //31-35
        matrix[8][1] = Cell(materialBlue,getCellPosition(8,1))
        matrix[8][2] = Cell(materialBlue,getCellPosition(8, 2))
        matrix[8][3] = Cell(materialBlue,getCellPosition(8,3))
        matrix[8][4] = Cell(materialBlue,getCellPosition(8,4))
        matrix[9][1] = Cell(materialBlue,getCellPosition(9,1))
        //36-40
        matrix[9][2] = Cell(materialBlue,getCellPosition(9,2))
        matrix[9][3] = Cell(materialBlue,getCellPosition(9,3))
        matrix[9][4] = Cell(materialBlue,getCellPosition(9,4))
        matrix[10][0] = Cell(materialBlue,getCellPosition(10,0))
        matrix[10][1] = Cell(materialBlue,getCellPosition(10,1))
        //41-45
        matrix[10][2] = Cell(materialBlue,getCellPosition(10,2))
        matrix[10][3] = Cell(materialBlue,getCellPosition(10,3))
        matrix[10][4] = Cell(materialBlue,getCellPosition(10,4))
        matrix[11][1] = Cell(materialBlue,getCellPosition(11,1))
        matrix[11][2] = Cell(materialBlue,getCellPosition(11,2))
        //46-50
        matrix[11][3] = Cell(materialBlue,getCellPosition(11,3))
        matrix[11][4] = Cell(materialBlue,getCellPosition(11,4))
        matrix[12][1] = Cell(materialBlue,getCellPosition(12,1))
        matrix[12][2] = Cell(materialBlue,getCellPosition(12,2))
        matrix[13][2] = Cell(materialBlue,getCellPosition(13,2))

        matrix[1][1]!!.belongsToPath = true

        generatePath()
    }
    fun getNearestCellToPos(pos: Vector3f): Vector2i
    {
        var nearest: Vector2i? = null;
        for(x in 0..matrix.size-1)
        {
            for(y in 0..matrix[x].size-1)
            {
                if(nearest == null && matrix[x][y] != null)
                {
                    nearest = Vector2i(x,y)
                    continue
                }

                if(matrix[x][y] != null && getCellPos(x,y).distance(pos) <= getCellPos(nearest!!.x, nearest!!.y).distance(pos))
                {
                    //var distOld = getCellPos(nearest!!.x, nearest!!.y).distance(pos)
                    //var newDist = getCellPos(x,y).distance(pos)
                    nearest = Vector2i(x,y)
                }
                /*
                else if(matrix[x][y] != null)
                {
                    var field31 = getCellPos(x,y)
                    var field11 = getCellPos(1,1)

                    var distOld = getCellPos(nearest!!.x, nearest!!.y).distance(pos)
                    var newDist = getCellPos(x,y).distance(pos)
                    var lookwkwkw = 022f
                }
                */
            }
        }
        return nearest!!
    }
    fun hidePath()
    {
        matrix.forEach { column -> column.forEach {
            it?.material = materialBlue} }
        matrix[1][1]!!.material = materialPink
        pathIsHidden = true
    }
    fun showPath()
    {
        matrix.forEach { column -> column.forEach {
            if (it?.belongsToPath == true) {
                it?.material = materialPink
            }
        }}
        pathIsHidden = false
    }
    fun generatePath()
    {
        var curPos = Vector2i(1, 1)
        while(!curPos.equals(Vector2i(8,4))) //12 2
        {
            var nextPos = calcRandomNextCell(curPos)
            if(nextPos.equals(Vector2i(-1,-1)))
            {
                resetAllCellStates()
                curPos = Vector2i(1, 1)
                continue
            }
            curPos = nextPos
            matrix[curPos.x][curPos.y]!!.material = materialPink
            matrix[curPos.x][curPos.y]!!.belongsToPath = true
        }
    }
    private fun resetAllCellStates()
    {
        matrix.forEach { column -> column.forEach { it?.belongsToPath = false
        it?.material = materialBlue} }
        matrix[1][1]!!.belongsToPath = true
        matrix[1][1]!!.material = materialPink
    }
    private fun checkSurroundingCellsForPink(pos: Vector2i): Boolean
    {
        if(getCellFromMatrix(pos) == null || getCellFromMatrix(pos)!!.belongsToPath)
        {
            return false
        }
        var pinkCounter = 0
        if(checkIfCellIsNullOrNotMarked(getCellAbove(pos)))
        {
            pinkCounter++
        }
        if(checkIfCellIsNullOrNotMarked(getDiagonalTopRight(pos)))
        {
            pinkCounter++
        }
        if(checkIfCellIsNullOrNotMarked(getDiagonalBottomRight(pos)))
        {
            pinkCounter++
        }
        if(checkIfCellIsNullOrNotMarked(getCellBelow(pos)))
        {
            pinkCounter++
        }
        if(checkIfCellIsNullOrNotMarked(getDiagonalBottomLeft(pos)))
        {
            pinkCounter++
        }
        if(checkIfCellIsNullOrNotMarked(getDiagonalTopLeft(pos)))
        {
            pinkCounter++
        }
        return pinkCounter < 2
    }
    private fun checkIfCellIsNullOrNotMarked(pos: Vector2i): Boolean
    {
        return getCellFromMatrix(pos) != null && getCellFromMatrix(pos)!!.belongsToPath
    }
    private fun getCellFromMatrix(pos: Vector2i): Cell?
    {
        if(inBounds(pos) != Vector2i(-1,-1))
        {
            return matrix[pos.x][pos.y]
        }
        return null
    }
    private fun calcRandomNextCell(pos: Vector2i): Vector2i {
        var rng = java.util.Random();
        for(i in 0..20) {
            var randomNumber = rng.nextInt(6)
            var newPos = when (randomNumber) {
                0 -> getCellAbove(pos);
                1 -> getDiagonalTopRight(pos);
                2 -> getDiagonalBottomRight(pos);
                3 -> getCellBelow(pos);
                4 -> getDiagonalBottomLeft(pos);
                5 -> getDiagonalTopLeft(pos);
                else -> Vector2i(-1, -1)
            }
            if(newPos.equals(Vector2i(-1,-1))) {
                continue
            }
            if (checkSurroundingCellsForPink(newPos)) {
                return newPos
            }
        }
        return Vector2i(-1,-1)
    }
    private fun getCellAbove(pos: Vector2i):Vector2i
    {
        var v = Vector2i(pos)
        return inBounds(v.add(Vector2i(0,-1)))
    }
    private fun getDiagonalTopRight(pos: Vector2i):Vector2i
    {
        var v = Vector2i(pos)
        if(v.x%2==0)
        {
            return inBounds(v.add(Vector2i(1,0)))
        }
        return inBounds(v.add(Vector2i(1,-1)))
    }
    private fun getDiagonalBottomRight(pos: Vector2i):Vector2i
    {
        var v = Vector2i(pos)
        if(v.x%2==0)
        {
            return inBounds(v.add(Vector2i(1,1)))
        }
        return inBounds(v.add(Vector2i(1,0)))
    }
    private fun getCellBelow(pos: Vector2i):Vector2i
    {
        var v = Vector2i(pos)
        return inBounds(v.add(Vector2i(0,1)))
    }
    private fun getDiagonalBottomLeft(pos: Vector2i):Vector2i
    {
        var v = Vector2i(pos)
        if(v.x%2==0)
        {
            return inBounds(v.add(Vector2i(-1,1)))
        }
        return inBounds(v.add(Vector2i(-1,0)))
    }
    private fun getDiagonalTopLeft(pos: Vector2i):Vector2i
    {
        var v = Vector2i(pos)
        if(v.x%2==0)
        {
            return inBounds(v.add(Vector2i(-1,0)))
        }
        return inBounds(v.add(Vector2i(-1,-1)))
    }
    private fun inBounds(pos: Vector2i): Vector2i
    {
        if(pos.x >= 0 && pos.y >= 0 && pos.x < size.x && pos.y < size.y)
        {
            return pos
        }
        return Vector2i(-1,-1)
    }
    private fun getCellPosition(column: Int, row: Int): Vector3f
    {
        var v = Vector3f()
        v.z = -40f*0.575f * row

        v.x = -35f*0.575f * column

        if(column%2==1)
        {
            v.z += 20f*0.575f
        }
        return v
    }
    fun getCellPos(column: Int, row: Int): Vector3f
    {
        if(matrix[column][row] == null)
        {
            return Vector3f(-1f,-1f,-1f)
        }
        return matrix[column][row]!!.getWorldPosition().mul(0.1f,0.1f,0.1f)
    }
    private fun loadCellModel():Renderable
    {
        var r = ModelLoader.loadModel("assets/models/hexxxx.obj",0f,0f,0.0f)

        var material = Material(
            texDiff,
            texEmitpink,
            texSpec,
            shininess = 60.0f,
            tcMultiplier = Vector2f(64.0f, 64.0f)
        )
        r!!.meshes.forEach { it.mat = material }

        r!!.scale(Vector3f(0.1f,0.1f,0.1f))
        r!!.translate(Vector3f(0f,5f,0f))
        return r
    }
    fun render(shaderProgram: ShaderProgram, shadowShader: ShaderProgram?) {
        matrix.forEach {
            column -> column.forEach {
                curCell ->
                    if(curCell != null) {
                        var pos = curCell.getWorldPosition()
                        var zzzz = pos.z
                        cell.translate(Vector3f(pos))
                        cell.rotate(Math.toRadians(-90.00f),0f,0f);
                        cell.meshes.forEach { inner -> inner.mat = curCell.material }
                        cell.render(shaderProgram, shadowShader)
                        cell.rotate(Math.toRadians(90.00f),0f,0f);
                        cell.translate(Vector3f(pos).negate())
                    }
            }
        }
    }
}