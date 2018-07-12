package io.jim.tesserapp.gl

import android.content.res.AssetManager
import android.opengl.GLES20
import io.jim.tesserapp.util.readStream

/**
 * GL shader. Type (i.e. vertex or fragment) is determined through file extension:
 * - `.vert`: Vertex shader
 * - `.frag`: Fragment shader
 *
 * @param assets Asset manager, required to open shader source file.
 * @param fileName Name of shader source file.
 */
class Shader(assets: AssetManager, val fileName: String) {
    
    /**
     * Actual handle retrieved from GL.
     */
    val handle: Int
    
    /**
     * Shader type, based on file name extension of [fileName].
     */
    private val shaderType = run {
        
        val extensionStartIndex = fileName.lastIndexOf('.').let {
            if (it == -1)
                throw RuntimeException("Malformed file name, no '.' found in: $fileName")
            if (it == fileName.lastIndex)
                throw RuntimeException("Missing file extension in: $fileName")
            it + 1
        }
        
        val extension = fileName.substring(extensionStartIndex)
        
        when (extension) {
            "vert" -> GLES20.GL_VERTEX_SHADER
            "frag" -> GLES20.GL_FRAGMENT_SHADER
            else -> throw RuntimeException("Unsupported source file extension in $fileName")
        }
    }
    
    init {
        // Generate handle.
        handle = GLES20.glCreateShader(shaderType)
        
        // Retrieve lines from shader file:
        GLES20.glShaderSource(handle, readStream(assets.open(fileName)
                ?: throw RuntimeException("Cannot open shader file $fileName")))
        
        // Compile shader and check for success:
        GLES20.glCompileShader(handle)
        
        GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, resultCode)
        if (GLES20.GL_TRUE != resultCode()) {
            throw GlError("Cannot compile vertex shader: " +
                    GLES20.glGetShaderInfoLog(handle))
        }
        
        GlError.check("Shader initialization")
    }
    
}
