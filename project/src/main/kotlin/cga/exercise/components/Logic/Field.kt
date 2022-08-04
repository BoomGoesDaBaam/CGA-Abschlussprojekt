package cga.exercise.components.Logic

import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.ModelLoader
import org.joml.Math
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f

class CellField(pathLength: Int):Transformable(){

    private var texDiff = Texture2D("assets/textures/ground_diff.png", true)
    private var texSpec = Texture2D("assets/textures/ground_spec.png", true)

    private var texEmitpink = Texture2D("assets/textures/pink.png", true)
    private var texEmitred = Texture2D("assets/textures/red.png", true)
    private var texEmitLightblue = Texture2D("assets/textures/lightblue.png", true)

    private var cell = loadCellModel()

    var pathIsHidden = false
    enum class state{
        REVEALING_PATH,
        WAIT_TO_HIDE,
        PLAYER_STEPS,
        FAILED,
        WON
    }
    var timer = 0.0f;
    var curRevealingPos = Vector2i(-1, -1)

    var startPos = Vector2i(1,1)
    var endPos = Vector2i(8,4)


    var gameState = state.REVEALING_PATH;

    var cells = ModelLoader.loadModel("assets/models/iceFloes.obj", 0f, 0f, 0f)

    private var texOceanEmissiv = Texture2D("assets/textures/lightblue.png", true)
    private var texOceanDiff = Texture2D("assets/textures/ocean_diffuse.png", true)
    private var texOceanSpec = Texture2D("assets/textures/ocean_diffuse.png", true)
    private var texOceanNormal = Texture2D("assets/textures/ocean_normal.png", true)

    private var materialPink = Material(texOceanDiff,texEmitpink,texOceanDiff)
    private var materialRed = Material(texOceanDiff,texEmitred,texOceanDiff)
    private var materialBlue = Material(texOceanDiff,texEmitLightblue,texOceanDiff)



    class Cell(var material: Material, pos: Vector3f? = null, var mesh: Mesh):Transformable()
    {
        var belongsToPath = false
        init {
            if(pos!=null){translate(pos!!)}
            mesh.mat = material
        }
        fun hasMaterial(material: Material): Boolean
        {
            return this.material == material
        }
        fun updateMaterial(newMaterial: Material)
        {
            material = newMaterial
            mesh.mat = newMaterial
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
        matrix[0][1] = Cell(materialBlue,getCellPosition(0,1), cells!!.meshes[1])
        matrix[0][2] = Cell(materialBlue,getCellPosition(0,2), cells!!.meshes[0])
        matrix[0][3] = Cell(materialBlue,getCellPosition(0,3), cells!!.meshes[2])
        matrix[1][1] = Cell(materialPink,getCellPosition(1,1), cells!!.meshes[3])
        matrix[1][2] = Cell(materialBlue,getCellPosition(1,2), cells!!.meshes[4])
        //6-10
        matrix[1][3] = Cell(materialBlue, getCellPosition(1,3), cells!!.meshes[5])
        matrix[1][4] = Cell(materialBlue,getCellPosition(1,4), cells!!.meshes[6])
        matrix[2][1] = Cell(materialBlue,getCellPosition(2,1), cells!!.meshes[7])
        matrix[2][2] = Cell(materialBlue,getCellPosition(2,2), cells!!.meshes[8])
        matrix[2][3] = Cell(materialBlue,getCellPosition(2,3), cells!!.meshes[9])
        //11-15
        matrix[2][4] = Cell(materialBlue, getCellPosition(2,4), cells!!.meshes[10])
        matrix[3][1] = Cell(materialBlue,getCellPosition(3,1), cells!!.meshes[11])
        matrix[3][2] = Cell(materialBlue,getCellPosition(3,2), cells!!.meshes[12])
        matrix[3][3] = Cell(materialBlue,getCellPosition(3,3), cells!!.meshes[13])
        matrix[3][4] = Cell(materialBlue,getCellPosition(3,4), cells!!.meshes[14])
        //16-20
        matrix[4][1] = Cell(materialBlue, getCellPosition(4,1), cells!!.meshes[15])
        matrix[4][2] = Cell(materialBlue,getCellPosition(4,2), cells!!.meshes[16])
        matrix[4][3] = Cell(materialBlue,getCellPosition(4,3), cells!!.meshes[17])
        matrix[4][4] = Cell(materialBlue,getCellPosition(4,4), cells!!.meshes[18])
        matrix[5][2] = Cell(materialBlue,getCellPosition(5,2), cells!!.meshes[19])
        //21-25
        matrix[5][3] = Cell(materialBlue, getCellPosition(5,3), cells!!.meshes[20])
        matrix[5][4] = Cell(materialBlue,getCellPosition(5,4), cells!!.meshes[21])
        matrix[6][1] = Cell(materialBlue,getCellPosition(6,1), cells!!.meshes[22])
        matrix[6][2] = Cell(materialBlue,getCellPosition(6,2), cells!!.meshes[23])
        matrix[6][3] = Cell(materialBlue,getCellPosition(6,3), cells!!.meshes[24])
        //26-30
        matrix[6][4] = Cell(materialBlue, getCellPosition(6,4), cells!!.meshes[25])
        matrix[7][1] = Cell(materialBlue,getCellPosition(7,1), cells!!.meshes[26])
        matrix[7][2] = Cell(materialBlue,getCellPosition(7,2), cells!!.meshes[27])
        matrix[7][3] = Cell(materialBlue,getCellPosition(7,3), cells!!.meshes[28])
        matrix[7][4] = Cell(materialBlue,getCellPosition(7,4), cells!!.meshes[29])
        //31-35
        matrix[8][1] = Cell(materialBlue,getCellPosition(8,1), cells!!.meshes[30])
        matrix[8][2] = Cell(materialBlue,getCellPosition(8, 2), cells!!.meshes[31])
        matrix[8][3] = Cell(materialBlue,getCellPosition(8,3), cells!!.meshes[32])
        matrix[8][4] = Cell(materialBlue,getCellPosition(8,4), cells!!.meshes[33])
        matrix[9][1] = Cell(materialBlue,getCellPosition(9,1), cells!!.meshes[34])
        //36-40
        matrix[9][2] = Cell(materialBlue,getCellPosition(9,2), cells!!.meshes[35])
        matrix[9][3] = Cell(materialBlue,getCellPosition(9,3), cells!!.meshes[36])
        matrix[9][4] = Cell(materialBlue,getCellPosition(9,4), cells!!.meshes[37])
        matrix[10][0] = Cell(materialBlue,getCellPosition(10,0), cells!!.meshes[38])
        matrix[10][1] = Cell(materialBlue,getCellPosition(10,1), cells!!.meshes[39])
        //41-45
        matrix[10][2] = Cell(materialBlue,getCellPosition(10,2), cells!!.meshes[40])
        matrix[10][3] = Cell(materialBlue,getCellPosition(10,3), cells!!.meshes[41])
        matrix[10][4] = Cell(materialBlue,getCellPosition(10,4), cells!!.meshes[42])
        matrix[11][1] = Cell(materialBlue,getCellPosition(11,1), cells!!.meshes[43])
        matrix[11][2] = Cell(materialBlue,getCellPosition(11,2), cells!!.meshes[44])
        //46-50
        matrix[11][3] = Cell(materialBlue,getCellPosition(11,3), cells!!.meshes[45])
        matrix[11][4] = Cell(materialBlue,getCellPosition(11,4), cells!!.meshes[46])
        matrix[12][1] = Cell(materialBlue,getCellPosition(12,1), cells!!.meshes[47])
        matrix[12][2] = Cell(materialBlue,getCellPosition(12,2), cells!!.meshes[48])
        matrix[13][2] = Cell(materialBlue,getCellPosition(13,2), cells!!.meshes[49])

        cells!!.rotate(0f,Math.toRadians(180f),0f)
        //field!!.scale(Vector3f(1.3f,1.3f,1.3f))
        cells!!.meshes[50].mat = Material(texOceanEmissiv, texOceanDiff, texOceanSpec, normal = texOceanNormal)
        startGame(pathLength)
    }
    fun startGame(pathLength: Int)
    {
        resetAllCellStates()
        matrix[1][1]!!.belongsToPath = true
        generatePath(pathLength)
        hidePath()
        gameState = state.REVEALING_PATH
        curRevealingPos = Vector2i(startPos)
    }
    fun update(dt: Float, pos: Vector3f)
    {
        when(gameState)
        {
            state.REVEALING_PATH -> revealPathStepByStep(dt)
            state.PLAYER_STEPS -> playerSteps(pos)
        }
    }
    fun continueGame()
    {
        if(gameState == state.WAIT_TO_HIDE)
        {
            hidePath()
            gameState = state.PLAYER_STEPS
        }
    }
    fun playerSteps(pos: Vector3f)
    {
        var nearestPos = getNearestCellToPos(pos)
        var nearestCell = matrix[nearestPos.x][nearestPos.y]
        if(nearestCell!!.getWorldPosition().add(Vector3f(0f,1f,0f)).distance(pos) < 1f)
        {
            if(nearestCell.belongsToPath)
            {
                nearestCell.updateMaterial(materialPink)
                if(nearestPos.equals(endPos))
                {
                    gameState = state.WON
                }
            }
            else
            {
                nearestCell.updateMaterial(materialRed)
                gameState = state.FAILED
            }
        }
    }
    fun revealPathStepByStep(dt: Float) {
        if (timer <= 0f) {
            var curPos = Vector2i(curRevealingPos)
            for (i in 0..5) {
                var curCheckPos = generateAdjacentCell(curPos, i)
                if (curCheckPos == Vector2i(-1, -1)) {
                    continue
                }
                var curCell = matrix[curCheckPos.x][curCheckPos.y]
                if (curCell != null && curCell.belongsToPath && curCell.hasMaterial(materialBlue)) {
                    curCell.updateMaterial(materialPink)
                    timer = 1f
                    curRevealingPos = curCheckPos
                    break;
                }
            }
        } else {
            timer -= dt;
        }
        if (curRevealingPos.equals(endPos)) {
            gameState = CellField.state.WAIT_TO_HIDE
        }
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

                if(matrix[x][y] != null && getCellPosition(x,y).distance(pos) <= getCellPosition(nearest!!.x, nearest!!.y).distance(pos))
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
            it?.updateMaterial(materialBlue)} }
        matrix[1][1]!!.updateMaterial(materialPink)
        pathIsHidden = true
    }
    fun showPath()
    {
        matrix.forEach { column -> column.forEach {
            if (it?.belongsToPath == true) {
                it?.updateMaterial(materialPink)
            }
        }}
        pathIsHidden = false
    }
    fun generatePath(pathLength: Int)
    {
        var curPos = startPos
        var curLength = 0
        while(!curPos.equals(endPos)) //12 2
        {
            var nextPos = calcRandomNextCell(curPos)
            if(nextPos.equals(Vector2i(-1,-1)) || curLength == pathLength || (nextPos.equals(endPos) && (curLength+1) != pathLength))
            {
                resetAllCellStates()
                curPos = Vector2i(startPos)
                curLength = 0
                continue
            }
            curLength++
            curPos = nextPos
            matrix[curPos.x][curPos.y]!!.updateMaterial(materialPink)
            matrix[curPos.x][curPos.y]!!.belongsToPath = true
        }
    }
    private fun resetAllCellStates()
    {
        matrix.forEach { column ->
            column.forEach { celle: Cell? ->
                run {
                    celle?.belongsToPath = false
                    celle?.updateMaterial(materialBlue)
                }
            }
        }
        matrix[1][1]!!.belongsToPath = true
        matrix[1][1]!!.updateMaterial(materialPink)
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
    private fun generateAdjacentCell(pos: Vector2i, randomNumber: Int): Vector2i {
        return when (randomNumber) {
            0 -> getCellAbove(pos);
            1 -> getDiagonalTopRight(pos);
            2 -> getDiagonalBottomRight(pos);
            3 -> getCellBelow(pos);
            4 -> getDiagonalBottomLeft(pos);
            5 -> getDiagonalTopLeft(pos);
            else -> Vector2i(-1, -1)
        }
    }
    private fun calcRandomNextCell(pos: Vector2i): Vector2i {
        var rng = java.util.Random();
        for(i in 0..20) {
            var randomNumber = rng.nextInt(6)
            var newPos = generateAdjacentCell(pos, randomNumber)
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

    fun getTopLeftOftCells(): Vector3f
    {
        return getWorldPosition().add(Vector3f(4*(0.5f * 1.75f),0f,0f))
    }
    fun getCellPosition(column: Int, row: Int): Vector3f
    {
        var v = getTopLeftOftCells()
        v.z += (0.866f * 1.75f) * column.toFloat()

        v.x -= (1.75f) * row.toFloat()

        if(column%2==1)
        {
            v.x -= -(1.75f) / 2
        }
        return v
    }
    /*
    fun getCellPos(column: Int, row: Int): Vector3f
    {
        if(matrix[column][row] == null)
        {
            return Vector3f(-1f,-1f,-1f)
        }
        return matrix[column][row]!!.getWorldPosition().mul(0.1f,0.1f,0.1f)
    }
    */

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
        cells!!.render(shaderProgram, shadowShader)
        /*
        matrix.forEach {
            column -> column.forEach {
                curCell ->
                    if(curCell != null) {
                        var pos = curCell.getWorldPosition()
                        cell.translate(Vector3f(pos))
                        cell.rotate(Math.toRadians(-90.00f),0f,0f);
                        cell.meshes.forEach { inner -> inner.mat = curCell.material }
                        cell.render(shaderProgram, shadowShader)
                        cell.rotate(Math.toRadians(90.00f),0f,0f);
                        cell.translate(Vector3f(pos).negate())
                    }
            }
        }*/
    }
}