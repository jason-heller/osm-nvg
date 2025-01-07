package examples;

import com.github.jh.nvgmap.MapData;
import com.github.jh.nvgmap.MapRegion;
import com.github.jh.nvgmap.MapRequester;
import com.github.jh.nvgmap.MapState;
import com.github.jh.nvgmap.nvg.NVGMap;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class BasicExample {

    private long window;

    private long ctx;
    private NVGMap map1, map2;

    private CompletableFuture<MapData> future;

    public void run() {
        init();

        map1 = new NVGMap(ctx, 0,0,400,400);
        map1.create(40.87141, -74.12478, 40.88555, -74.09110);

        map2 = new NVGMap(ctx, 450,200,200,200);
        map2.create(40.785692, -74.065425, 40.792775, -74.048581);

        loop();

        map1.dispose(ctx);
        map2.dispose(ctx);

        NanoVGGL3.nvgDelete(ctx);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        GL.createCapabilities();

        ctx = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);

        glfwShowWindow(window);
    }

    private void loop() {

        glClearColor(0.4f, 0.4f, 0.4f, 0.0f);

        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Draw the map
            NanoVG.nvgBeginFrame(ctx, 800, 600, 1);

            map1.draw(ctx);
            map2.draw(ctx);

            NanoVG.nvgEndFrame(ctx);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new BasicExample().run();
    }

}
