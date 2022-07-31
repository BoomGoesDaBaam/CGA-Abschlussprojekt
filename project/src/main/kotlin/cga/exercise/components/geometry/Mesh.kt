package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
import org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30


/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexdata plain float array of vertex data
 * @param indexdata  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created by Fabian on 16.09.2017.
 */
class Mesh(vertexdata: FloatArray, indexdata: IntArray, attributes: Array<VertexAttribute>, var mat:Material? = null) {
    //private data
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    private var indexcount = 0

    init {

        indexcount = indexdata.size //Anzahl an Ecken der Dreiecke

        // setup VAO //
        vao = glGenVertexArrays();          //generate VertexArrays and return access id
        glBindVertexArray(vao);             //activate id => Zustandmaschine

        // setup VBO //
        vbo = glGenBuffers();                                       //generate VBO and return access id
        glBindBuffer(GL_ARRAY_BUFFER, vbo);                         //activate id => Zustandmaschine
        //GL_ARRAY_BUFFER => Vertex attributes. Spezifiziert die Zieladresse, auf das das vbo gebunden wird
        glBufferData(GL_ARRAY_BUFFER, vertexdata, GL_STATIC_DRAW);
        //Daten auf die Grafikkarte laden. Erstellt und Initlisiert ein Buffer Object

        for(i in 0..attributes.size-1)
        {
            glEnableVertexAttribArray(i);                           //activiert einen VertexArray. Aktiviert Attribute.(0=position, 1=farbe
            glVertexAttribPointer(i, attributes[i].n, attributes[i].type, false, attributes[i].stride, attributes[i].offset.toLong());
            //spezifizert den Ort und das Datenformat der Vertexattribute, sowie deren Eigentschaften
        }

        ibo = glGenBuffers();                               //generiere IBO and gebe id zurück
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);         //generiere IBO and gebe id zurück
        //GL_ELEMENT_ARRAY_BUFFER => Speichert "Vertex array indices" (ibo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexdata, GL_STATIC_DRAW);
        //Daten auf die Grafikkarte laden. Erstellt und Initlisiert ein Buffer Object
    }

    /**
     * renders the mesh
     */
    fun render() {
        glBindVertexArray(vao) // Spezifiziert, welches VAO verwendet werden soll
        glDrawElements(GL_TRIANGLES, indexcount, GL11.GL_UNSIGNED_INT, 0)
        //Spezifiziert, was gezeichnet werden soll: GL_TRIANGLES => es sollen Dreiecke gezeichnet werden
        //                  indexcount   => gibt an, wieviele eckpunkte verwendet werden sollen
        //                  type => Werte in der Liste indexdata sind vom Typ int
        //                  indicies => startpositin im ibo
        glBindVertexArray(0)
    }
    fun render(shaderProgram: ShaderProgram) {
        mat?.bind(shaderProgram)
        render()
    }
    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}