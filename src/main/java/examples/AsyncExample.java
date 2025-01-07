package examples;

import com.github.jh.nvgmap.*;
import com.github.jh.nvgmap.nvg.NVGMap;
import org.lwjgl.glfw.*;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class AsyncExample {

    private long window;

    private long ctx;
    private NVGMap map;

    private CompletableFuture<MapData> future;

    public void run() {
        init();

        map = new NVGMap(ctx, 0,0,800,600);

        // Puts the map into a 'waiting' state - will automatically change upon receiving data
        map.setState(MapState.WAITING);

        // Create a future to load the map asynchronously
        future = CompletableFuture.supplyAsync(() -> {
            MapRegion mapRegion = new MapRegion(40.776812, -73.973984, 40.783897, -73.957139);

            MapRequester requester = new MapRequester()
                    .setMapRegion(mapRegion)
                    .query("way->.ways;\n" +
                            "rel[type=multipolygon]->.polys;\n" +
                            "(.ways; .polys;)");

            return requester.retrieveMapData();
        });

        loop();

        map.dispose(ctx);

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

            // Poll to see if the map data has been received
            if (future.isDone()) {
                try {
                    MapData mapData = future.get();
                    map.create(mapData);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Draw the map
            NanoVG.nvgBeginFrame(ctx, 800, 600, 1);
            map.draw(ctx);
            NanoVG.nvgEndFrame(ctx);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new AsyncExample().run();
    }

}
