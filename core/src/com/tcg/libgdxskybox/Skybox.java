package com.tcg.libgdxskybox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Skybox implements Disposable {

    private static final String vertexShader = "#version 120\n" +
            "\n" +
            "attribute vec3 a_position;\n" +
            "uniform mat4 u_projection;\n" +
            "\n" +
            "varying vec3 texCoord;\n" +
            "\n" +
            "void main() {\n" +
            "    texCoord = a_position;\n" +
            "    vec4 pos = u_projection * vec4(a_position, 1.0);\n" +
            "    gl_Position = pos.xyww;\n" +
            "}\n";

    private static final String fragmentShader = "#version 120\n" +
            "\n" +
            "varying vec3 texCoord;\n" +
            "uniform samplerCube skybox;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_FragColor = textureCube(skybox, texCoord);\n" +
            "}\n";

    private final Matrix4 view;
    private final Matrix4 projection;
    private final Matrix4 combined;

    private final Cubemap cubemap;
    private final Mesh mesh;

    private final ShaderProgram skyboxShader;

    public Skybox(FileHandle parent) {
        this(parent.child("right.png"),
                parent.child("left.png"),
                parent.child("top.png"),
                parent.child("bottom.png"),
                parent.child("front.png"),
                parent.child("back.png"));
    }

    public Skybox(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ,
                  FileHandle negativeZ) {

        this.cubemap = new Cubemap(positiveX, negativeX, positiveY, negativeY, positiveZ, negativeZ);
        final float[] vertices = new float[]{
                // top
                -1, 1, -1,
                1, 1, -1,
                1, 1, 1,
                -1, 1, 1,
                // bottom
                -1, -1, -1,
                -1, -1, 1,
                1, -1, 1,
                1, -1, -1,
                // right
                -1, -1, -1,
                -1, 1, -1,
                -1, 1, 1,
                -1, -1, 1,
                // left
                1, -1, -1,
                1, -1, 1,
                1, 1, 1,
                1, 1, -1,
                // back
                -1, -1, -1,
                1, -1, -1,
                1, 1, -1,
                -1, 1, -1,
                // front
                -1, -1, 1,
                -1, 1, 1,
                1, 1, 1,
                1, -1, 1
        };

        final short[] indices = new short[]{
                // top
                0, 1, 2,
                0, 2, 3,
                // bottom
                4, 5, 6,
                4, 6, 7,
                // right
                8, 9, 10,
                8, 10, 11,
                // left
                12, 13, 14,
                12, 14, 15,
                // back
                16, 17, 18,
                16, 18, 19,
                // front
                20, 21, 22,
                20, 22, 23
        };
        this.mesh = new Mesh(true, vertices.length, indices.length,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));

        this.mesh.setVertices(vertices);
        this.mesh.setIndices(indices);

        this.skyboxShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!this.skyboxShader.isCompiled())
            throw new GdxRuntimeException("Unable to compile shader: " + this.skyboxShader.getLog());

        this.view = new Matrix4();
        this.projection = new Matrix4();
        this.combined = new Matrix4();




    }

    public void render(PerspectiveCamera camera) {
        this.updateMatrices(camera);
        this.skyboxShader.bind();
        this.skyboxShader.setUniformi("skybox", 0);
        this.skyboxShader.setUniformMatrix("u_projection", combined);
        this.cubemap.bind();
        this.mesh.render(skyboxShader, GL20.GL_TRIANGLES);

    }

    private void updateMatrices(PerspectiveCamera camera) {
        final float aspect = camera.viewportWidth / camera.viewportHeight;
        view.setToLookAt(new Vector3(), new Vector3(camera.direction), new Vector3(camera.up));
        projection.setToProjection(0.1f, 1.0f, camera.fieldOfView, aspect);
        combined.set(projection);
        Matrix4.mul(combined.val, view.val);
    }

    @Override
    public void dispose() {
        this.cubemap.dispose();
        this.mesh.dispose();
        this.skyboxShader.dispose();
    }
}
