package marlin.auber.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class AnimSheet implements Json.Serializable {
    private Texture tex;
    private int sheetRows;
    private int sheetCols;
    private int frames;
    private float totalTime;
    private Animation<TextureRegion> anim;

    private float stateTime = 0f;

    public static AnimSheet create(FileHandle path) {
        Json json = new Json();
        return json.fromJson(AnimSheet.class, path);
    }

    public TextureRegion tickAndGet(boolean looping) {
        this.stateTime += Gdx.graphics.getDeltaTime();
        return this.anim.getKeyFrame(this.stateTime, looping);
    }

    @Override
    public void write(Json json) {
        throw new RuntimeException("no");
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.tex = new Texture(Gdx.files.internal(jsonData.getString("spritesheet")));
        this.sheetRows = jsonData.getInt("rows");
        this.sheetCols = jsonData.getInt("cols");
        this.frames = jsonData.getInt("frames");
        this.totalTime = jsonData.getFloat("totalTime");

        TextureRegion[][] tmp = TextureRegion.split(
                this.tex,
                this.tex.getWidth() / this.sheetRows,
                this.tex.getHeight() / this.sheetCols
        );
        TextureRegion[] frames = new TextureRegion[this.sheetRows * this.sheetCols];
        int index = 0;
        for (int i = 0; i < this.sheetRows; i++) {
            for (int j = 0; j < this.sheetCols; j++) {
                frames[index++] = tmp[i][j];
                if (index > this.frames) {
                    break;
                }
            }
        }
        Animation.PlayMode mode;
        switch (jsonData.getString("playMode", "default")) {
            case "loop":
                mode = Animation.PlayMode.LOOP;
                break;
            case "loopReverse":
                mode = Animation.PlayMode.LOOP_REVERSED;
                break;
            case "oneshot":
                mode = Animation.PlayMode.NORMAL;
                break;
            case "oneshotReverse":
                mode = Animation.PlayMode.REVERSED;
            case "pingpong":
                mode = Animation.PlayMode.LOOP_PINGPONG;
                break;
            default:
                mode = Animation.PlayMode.LOOP;
        }

        this.anim = new Animation<TextureRegion>(
            this.totalTime / this.frames,
                frames
        );
        this.anim.setPlayMode(mode);
    }
}
