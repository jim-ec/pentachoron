package io.jim.tesserapp.cpp.graphics

import android.content.res.AssetManager
import android.opengl.GLES20

/**
 * GL shader. Type (i.e. vertex or fragment) is determined through file extension:
 * - `.vert`: Vertex shader
 * - `.frag`: Fragment shader
 *
 * @param assets Asset manager, required to open shader source file.
 * @param fileName Name of shader source file.
 */
class GlShader(assets: AssetManager, fileName: String) {
    
    /**
     * Actual handle retrieved from GL.
     */
    val handle: Int
    
    init {
        // Generate handle. Type is deduced from file extension.
        handle = GLES20.glCreateShader(
                when (fileName.substring(fileName.lastIndexOf('.').also { index ->
                    if (index == -1)
                        throw RuntimeException("Malformed file name, no '.' found in: $fileName")
                    if (index == fileName.lastIndex)
                        throw RuntimeException("Missing file extension in: $fileName")
                } + 1)) {
                    "vert" -> GLES20.GL_VERTEX_SHADER
                    "frag" -> GLES20.GL_FRAGMENT_SHADER
                    else -> throw RuntimeException("Unsupported source file extension in $fileName")
                })
        
        // Retrieve lines from shader file:
        GLES20.glShaderSource(handle, (assets.open(fileName)
                ?: throw RuntimeException("Cannot open shader file $fileName"))
                .bufferedReader().useLines { sequence: Sequence<String> ->
                    sequence.reduce { a, b -> "$a\n$b" }
                })
        
        // Compile shader and check for success:
        GLES20.glCompileShader(handle)
    
        GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, resultCode)
        if (GLES20.GL_TRUE != resultCode()) {
            throw GlException("Cannot compile vertex shader: " +
                    GLES20.glGetShaderInfoLog(handle))
        }
    
        GlException.check("Shader initialization")
    }
    
}
