package com.packtpub.libgdx.bludbourne;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class PlayerGraphicsComponent extends GraphicsComponent {

    private static final String TAG = PlayerGraphicsComponent.class.getSimpleName();

    private static final String _defaultSpritePath = "sprites/characters/Warrior.png";

    private Vector2 _currentPosition;
    private Entity.State _currentState;
    private Entity.Direction _currentDirection;

    private Animation _walkLeftAnimation;
    private Animation _walkRightAnimation;
    private Animation _walkUpAnimation;
    private Animation _walkDownAnimation;

    private Json _json;

    private float _frameTime = 0f;
    private TextureRegion _currentFrame = null;

    public PlayerGraphicsComponent(){
        _currentPosition = new Vector2(0,0);
        _currentState = Entity.State.WALKING;
        _currentDirection = Entity.Direction.DOWN;

        Utility.loadTextureAsset(_defaultSpritePath);
        Texture _texture = Utility.getTextureAsset(_defaultSpritePath);

        Array<GridPoint2> downGridPoints;
        Array<GridPoint2> leftGridPoints;
        Array<GridPoint2> rightGridPoints;
        Array<GridPoint2> upGridPoints;

        downGridPoints = new Array<GridPoint2>();
        downGridPoints.add(new GridPoint2(0,0));
        downGridPoints.add(new GridPoint2(0,1));
        downGridPoints.add(new GridPoint2(0,2));
        downGridPoints.add(new GridPoint2(0,3));

        _walkDownAnimation = loadAnimation(_texture, downGridPoints );

        leftGridPoints = new Array<GridPoint2>();
        leftGridPoints.add(new GridPoint2(1,0));
        leftGridPoints.add(new GridPoint2(1,1));
        leftGridPoints.add(new GridPoint2(1,2));
        leftGridPoints.add(new GridPoint2(1,3));

        _walkLeftAnimation = loadAnimation(_texture, leftGridPoints );

        rightGridPoints = new Array<GridPoint2>();
        rightGridPoints.add(new GridPoint2(2,0));
        rightGridPoints.add(new GridPoint2(2,1));
        rightGridPoints.add(new GridPoint2(2,2));
        rightGridPoints.add(new GridPoint2(2,3));

        _walkRightAnimation = loadAnimation(_texture, rightGridPoints );

        upGridPoints = new Array<GridPoint2>();
        upGridPoints.add(new GridPoint2(3,0));
        upGridPoints.add(new GridPoint2(3,1));
        upGridPoints.add(new GridPoint2(3,2));
        upGridPoints.add(new GridPoint2(3,3));

        _walkUpAnimation = loadAnimation(_texture, upGridPoints );

        _json = new Json();
    }

    @Override
    public void receiveMessage(String message) {
        //Gdx.app.debug(TAG, "Got message " + message);
        String[] string = message.split(MESSAGE_TOKEN);

        if( string.length == 0 ) return;

        //Specifically for messages with 1 object payload
        if( string.length == 2 ) {
            if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_POSITION.toString())) {
                _currentPosition = _json.fromJson(Vector2.class, string[1]);
            } else if (string[0].equalsIgnoreCase(MESSAGE.INIT_START_POSITION.toString())) {
                _currentPosition = _json.fromJson(Vector2.class, string[1]);
            } else if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_STATE.toString())) {
                _currentState = _json.fromJson(Entity.State.class, string[1]);
            } else if (string[0].equalsIgnoreCase(MESSAGE.CURRENT_DIRECTION.toString())) {
                _currentDirection = _json.fromJson(Entity.Direction.class, string[1]);
            }
        }
    }

    @Override
    public void update(Entity entity, Batch batch, float delta){
        _frameTime = (_frameTime + delta)%5; //Want to avoid overflow

        //Look into the appropriate variable when changing position
        switch (_currentDirection) {
            case DOWN:
                if (_currentState == Entity.State.WALKING) {
                    _currentFrame = _walkDownAnimation.getKeyFrame(_frameTime);
                } else {
                    _currentFrame = _walkDownAnimation.getKeyFrames()[0];
                }
                break;
            case LEFT:
                if (_currentState == Entity.State.WALKING) {
                    _currentFrame = _walkLeftAnimation.getKeyFrame(_frameTime);
                }else{
                    _currentFrame = _walkLeftAnimation.getKeyFrames()[0];
                }
                break;
            case UP:
                if (_currentState == Entity.State.WALKING) {
                    _currentFrame = _walkUpAnimation.getKeyFrame(_frameTime);
                }else{
                    _currentFrame = _walkUpAnimation.getKeyFrames()[0];
                }
                break;
            case RIGHT:
                if (_currentState == Entity.State.WALKING) {
                    _currentFrame = _walkRightAnimation.getKeyFrame(_frameTime);
                }else{
                    _currentFrame = _walkRightAnimation.getKeyFrames()[0];
                }
                break;
            default:
                break;
        }

        batch.begin();
        batch.draw(_currentFrame, _currentPosition.x, _currentPosition.y, 1, 1);
        batch.end();
    }

    @Override
    public void dispose(){
        Utility.unloadAsset(_defaultSpritePath);
    }

}
