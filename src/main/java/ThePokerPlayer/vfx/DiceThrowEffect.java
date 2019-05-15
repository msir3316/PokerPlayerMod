package ThePokerPlayer.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class DiceThrowEffect extends AbstractGameEffect {
	public static final float STOP_DUR = 3.2f;
	public static final float WAIT_DUR = 3.7f;
	public static final float DICE_SIZE = 40.0f;
	public static final float Z_RATIO = 2.0f;

	private float t;

	private int[] spots;
	public int diceResult = 0;

	public static Texture[] faceTexWithSpot;
	public static final int F_WIDTH = 32;
	public static final int F_HEIGHT = 32;
	public static final int[][] SPOTS_X = new int[][]{
			new int[0],
			new int[]{16},
			new int[]{10, 22},
			new int[]{8, 16, 24},
			new int[]{8, 8, 24, 24},
			new int[]{8, 8, 16, 24, 24},
			new int[]{10, 10, 10, 22, 22, 22},
			new int[]{12, 20, 8, 16, 24, 12, 20},
			new int[]{8, 16, 24, 12, 20, 8, 16, 24},
			new int[]{8, 16, 24, 8, 16, 24, 8, 16, 24},
			new int[]{8, 16, 24, 4, 12, 20, 28, 8, 16, 24},
	};
	public static final int[][] SPOTS_Y = new int[][]{
			new int[0],
			new int[]{16},
			new int[]{10, 22},
			new int[]{8, 16, 24},
			new int[]{8, 24, 8, 24},
			new int[]{8, 24, 16, 8, 24},
			new int[]{8, 16, 24, 8, 16, 24},
			new int[]{8, 8, 16, 16, 16, 24, 24},
			new int[]{8, 8, 8, 16, 16, 24, 24, 24},
			new int[]{8, 8, 8, 16, 16, 16, 24, 24, 24},
			new int[]{8, 8, 8, 16, 16, 16, 16, 24, 24, 24},
	};

	static {
		faceTexWithSpot = new Texture[11];
		for (int i = 1; i <= 10; i++) {
			Pixmap px = new Pixmap(F_WIDTH, F_HEIGHT, Pixmap.Format.RGBA8888);
			for (int x = 0; x < px.getWidth(); x++) {
				for (int y = 0; y < px.getHeight(); y++) {
					if (x == 0 || y == 0 || x == px.getWidth() - 1 || y == px.getHeight()) {
						px.setColor(0.0f, 0.0f, 0.0f, 1.0f);
					} else {
						px.setColor(0.5f, 0.5f, 1.0f, 1.0f);
					}
					px.drawPixel(x, y);
				}
			}
			drawSpots(px, i);
			faceTexWithSpot[i] = new Texture(px);
		}
	}

	// JBullet
	private BroadphaseInterface broadphase = new DbvtBroadphase();
	private DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
	private CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
	private SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
	private DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
	private RigidBody diceRigidBody;

	private static void drawSpots(Pixmap px, int num) {
		for (int i = 0; i < num; i++) {
			px.setColor(0.0f, 0.0f, 0.3f, 1.0f);
			px.drawPixel(SPOTS_X[num][i] - 2, SPOTS_Y[num][i] - 1);
			px.drawPixel(SPOTS_X[num][i] - 2, SPOTS_Y[num][i]);
			px.drawPixel(SPOTS_X[num][i] - 1, SPOTS_Y[num][i] - 2);
			px.drawPixel(SPOTS_X[num][i] - 1, SPOTS_Y[num][i] + 1);
			px.drawPixel(SPOTS_X[num][i], SPOTS_Y[num][i] - 2);
			px.drawPixel(SPOTS_X[num][i], SPOTS_Y[num][i] + 1);
			px.drawPixel(SPOTS_X[num][i] + 1, SPOTS_Y[num][i] - 1);
			px.drawPixel(SPOTS_X[num][i] + 1, SPOTS_Y[num][i]);
			px.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			px.drawPixel(SPOTS_X[num][i] - 1, SPOTS_Y[num][i] - 1);
			px.drawPixel(SPOTS_X[num][i] - 1, SPOTS_Y[num][i]);
			px.drawPixel(SPOTS_X[num][i], SPOTS_Y[num][i] - 1);
			px.drawPixel(SPOTS_X[num][i], SPOTS_Y[num][i]);
		}
	}

	public static final int[][] faceIndex = new int[][]{
			new int[]{0, 1, 3, 2, 4},
			new int[]{0, 1, 5, 4, 2},
			new int[]{0, 4, 6, 2, 1},
			new int[]{1, 5, 7, 3, 0},
			new int[]{2, 3, 7, 6, 0},
			new int[]{5, 4, 6, 7, 1}
	};
	private boolean[] faceVisible;
	private Vector3[] vertex;
	private Random rngCopy;

	public DiceThrowEffect(boolean upgraded) {
		spots = new int[]{1, 2, 3, 4, 5, 6};
		if (upgraded) {
			int spotRng = AbstractDungeon.cardRandomRng.random(18 * 16 * 14 * 12 - 1);
			for (int i = 10; i >= 7; i--) {
				int div = i * 2 - 2;
				int pos = spotRng % div;
				if (pos < 6) {
					spots[pos] = i;
				}
				spotRng /= div;
			}
		} else {
			AbstractDungeon.cardRandomRng.random(48);
		}

		rngCopy = new Random(Settings.seed, AbstractDungeon.cardRandomRng.counter);
		t = 0;

		vertex = new Vector3[8];
		faceVisible = new boolean[6];

		dynamicsWorld.setGravity(new Vector3f(0, 0, -10));

		CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 0, 1), 1);
		CollisionShape wallShape = new StaticPlaneShape(new Vector3f(0, -1, 0), 1);
		CollisionShape wall2Shape = new StaticPlaneShape(new Vector3f(1, 0, 0), 1);
		CollisionShape wall3Shape = new StaticPlaneShape(new Vector3f(-1, 0, 0), 1);
		CollisionShape diceShape = new BoxShape(new Vector3f(1, 1, 1));

		DefaultMotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 0, -1), 1.0f)));
		DefaultMotionState wallMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 5.0f, 0), 1.0f)));
		DefaultMotionState wall2MotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(-7, 0, 0), 1.0f)));
		DefaultMotionState wall3MotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(6, 0, 0), 1.0f)));

		RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
		RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI);
		groundRigidBody.setFriction(0.4f);
		dynamicsWorld.addRigidBody(groundRigidBody);
		RigidBodyConstructionInfo wallRigidBodyCI = new RigidBodyConstructionInfo(0, wallMotionState, wallShape, new Vector3f(0, 0, 0));
		RigidBody wallRigidBody = new RigidBody(wallRigidBodyCI);
		wallRigidBody.setFriction(0.01f);
		dynamicsWorld.addRigidBody(wallRigidBody);
		RigidBodyConstructionInfo wall2RigidBodyCI = new RigidBodyConstructionInfo(0, wall2MotionState, wall2Shape, new Vector3f(0, 0, 0));
		RigidBody wall2RigidBody = new RigidBody(wall2RigidBodyCI);
		wall2RigidBody.setFriction(0.01f);
		dynamicsWorld.addRigidBody(wall2RigidBody);
		RigidBodyConstructionInfo wall3RigidBodyCI = new RigidBodyConstructionInfo(0, wall3MotionState, wall3Shape, new Vector3f(0, 0, 0));
		RigidBody wall3RigidBody = new RigidBody(wall3RigidBodyCI);
		wall3RigidBody.setFriction(0.01f);
		dynamicsWorld.addRigidBody(wall3RigidBody);

		Vector3f position = new Vector3f(rngCopy.random() * 10.0f - 5.0f, -16, 5);
		DefaultMotionState fallMotionState = new DefaultMotionState(new Transform(new Matrix4f(
				getRandomRotation(rngCopy), position, 1.0f)));

		int mass = 1;

		Vector3f fallInertia = new Vector3f(0, 0, 0);
		diceShape.calculateLocalInertia(mass, fallInertia);

		RigidBodyConstructionInfo diceRigidBodyCI = new RigidBodyConstructionInfo(mass, fallMotionState, diceShape, fallInertia);
		diceRigidBody = new RigidBody(diceRigidBodyCI);
		diceRigidBody.setLinearVelocity(new Vector3f(position.x * -0.35f, 12 + rngCopy.random() * 6.0f, 5.0f));
		diceRigidBody.setAngularVelocity(new Vector3f(
				(rngCopy.random() - 0.5f) * 10.0f,
				(rngCopy.random() - 0.5f) * 10.0f,
				(rngCopy.random() - 0.5f) * 10.0f));
		diceRigidBody.setFriction(0.5f);

		dynamicsWorld.addRigidBody(diceRigidBody);
	}

	private Quat4f getRandomRotation(Random rng) {
		float u1 = rng.random();
		float u2 = rng.random();
		float u3 = rng.random();

		double sqrtu1 = Math.sqrt(u1);
		double invSqrtu1 = Math.sqrt(1 - u1);
		double x = invSqrtu1 * Math.sin(Math.PI * 2 * u2);
		double y = invSqrtu1 * Math.cos(Math.PI * 2 * u2);
		double z = sqrtu1 * Math.sin(Math.PI * 2 * u3);
		double w = sqrtu1 * Math.cos(Math.PI * 2 * u3);

		return new Quat4f((float) x, (float) y, (float) w, (float) z);
	}

	@Override
	public void update() {
		float dt = Gdx.graphics.getDeltaTime() * (Settings.FAST_MODE ? 1.5f : 1.0f);
		t += dt;
		if (t <= STOP_DUR) {
			dynamicsWorld.stepSimulation(dt, 10);

			setVertices();
		} else if (t >= WAIT_DUR) {
			this.isDone = true;
		}
	}

	private void setVertices() {
		Quat4f rot = new Quat4f(0, 0, 0, 1);
		diceRigidBody.getOrientation(rot);
		Quaternion betterQuat = new Quaternion(rot.x, rot.y, rot.z, rot.w);

		Vector3 v1 = new Vector3(1, 0, 0);
		Vector3 v2 = new Vector3(0, 1, 0);
		Vector3 v3 = new Vector3(0, 0, 1);

		v1 = betterQuat.transform(v1);
		v2 = betterQuat.transform(v2);
		v3 = betterQuat.transform(v3);

		for (int i = 0; i < 8; i++) {
			vertex[i] = new Vector3(
					(i % 2 == 0 ? v1.x : -v1.x) + (i / 2 % 2 == 0 ? v2.x : -v2.x) + (i / 4 % 2 == 0 ? v3.x : -v3.x),
					(i % 2 == 0 ? v1.y : -v1.y) + (i / 2 % 2 == 0 ? v2.y : -v2.y) + (i / 4 % 2 == 0 ? v3.y : -v3.y),
					(i % 2 == 0 ? v1.z : -v1.z) + (i / 2 % 2 == 0 ? v2.z : -v2.z) + (i / 4 % 2 == 0 ? v3.z : -v3.z)
			);
		}
		float max = 0;
		for (int i = 0; i < 6; i++) {
			float dist = vertex[faceIndex[i][4]].z - vertex[faceIndex[i][0]].z;
			faceVisible[i] = dist > 0;
			if (dist > max) {
				max = dist;
				diceResult = spots[i];
			}
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		sb.setColor(Color.WHITE.cpy());
		for (int i = 0; i < 6; i++) {
			if (faceVisible[i]) {
				sb.draw(faceTexWithSpot[spots[i]], getVertices(faceIndex[i], Color.WHITE.cpy()), 0, 20);
			}
		}
	}

	private float[] getVertices(int[] index, Color color) {
		float[] result = new float[20];
		for (int i = 0; i < 4; i++) {
			Vector3f v = new Vector3f();
			diceRigidBody.getCenterOfMassPosition(v);
			result[i * 5] = (vertex[index[i]].x + v.x) * DICE_SIZE * this.scale + Settings.WIDTH * 0.33f;
			result[i * 5 + 1] = (vertex[index[i]].y + v.y + v.z * Z_RATIO) * DICE_SIZE * this.scale + Settings.HEIGHT / 2.0f;
			result[i * 5 + 2] = Color.toFloatBits(color.r, color.g, color.b, color.a);
			result[i * 5 + 3] = (i % 3 == 0) ? 0 : 1;
			result[i * 5 + 4] = i / 2;
		}

		return result;
	}

	@Override
	public void dispose() {
	}
}
