package com.tcg.libgdxskybox;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;

public class LibGDXSkybox extends ApplicationAdapter {

	public final float WORLD_WIDTH = 1920;
	public final float WORLD_HEIGHT = 1080;

	public PerspectiveCamera camera;

	public Model model;
	public ModelInstance instance;
	public ModelBatch modelBatch;

	public Environment environment;

	public CameraInputController camController;


	public Skybox skybox;

	@Override
	public void create () {
		modelBatch = new ModelBatch();
		camera = new PerspectiveCamera(67, WORLD_WIDTH, WORLD_HEIGHT);
		camera.position.set(10f, 10f, 10f);
		camera.lookAt(0, 0, 0);
		camera.near = 1f;
		camera.far = 300f;
		camera.update();

		final ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createBox(
				5f, 5f, 5f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
		);
		instance = new ModelInstance(model);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));


		camController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(camController);

		this.skybox = new Skybox(Gdx.files.internal("lightblue"));

	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1, true);

		camController.update();

		this.skybox.render(this.camera);

		modelBatch.begin(camera);
		modelBatch.render(instance, environment);
		modelBatch.end();

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void dispose () {
		modelBatch.dispose();
		model.dispose();
		this.skybox.dispose();
	}
}

