package cga.exercise.game

class AnimatedCharacter(var duration: Float = 1f, charType: Int): Character(charType) {
    var frames = mutableListOf<KeyFrame>()
    var curKeyFrame = 0
    var passedTime = 0.0f
    var AnimationStoppedKeyFrame =  KeyFrame(0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f)
    var animationStopped = true

    class KeyFrame(var leftFootRotation: Float, var rightFootRotation: Float,
                   var leftLowerLegRotation: Float, var rightLowerLegRotation: Float,
                   var leftUpperLegRotation: Float, var rightUpperLegRotation: Float,
                    var leftHandRotation: Float, var rightHandRotation: Float,
                    var leftLowerArmRotation: Float, var rightLowerArmRotation: Float,
                    var leftUpperArmRotation: Float, var rightUpperArmRotation: Float,
                    var headrotation: Float)
    {

    }
    fun stopAnimation()
    {
        animationStopped = true
    }
    fun startAnimation()
    {
        animationStopped = false
        passedTime = 0.0f
    }
    fun addKeyFrame(frame: KeyFrame)
    {
        frames.add(frame)
    }
    fun getNextKeyFrame(): KeyFrame
    {
        if(animationStopped)
        {
            return AnimationStoppedKeyFrame
        }
        return if(curKeyFrame+1 <= frames.size-1)
            frames[curKeyFrame+1]
        else
            frames[0]
    }
    fun getInterpolation(lastAngle: Float, nextAngle: Float, factor: Float):Float
    {
        var sign = 1
        if(nextAngle < lastAngle ) sign = -1
        var angle = Math.abs(lastAngle - nextAngle)
        return lastAngle + angle * factor * sign
    }
    fun update(dt: Float)
    {
        if(!animationStopped) {
            passedTime += dt
            while (passedTime > duration) {
                curKeyFrame++
                passedTime -= duration
            }
            if (curKeyFrame == frames.size) curKeyFrame = 0

            var vertexdata: FloatArray = player!!.meshes[0].vertexdata.clone()


            var lastKeyFrame = frames[curKeyFrame]
            var nextKeyFrame = getNextKeyFrame()
            var factor = passedTime / duration

            //abs(45-(-45)) = 90
            //oldAngle + 90*factor*sign

            var interpolated = getInterpolation(lastKeyFrame.leftFootRotation, nextKeyFrame.leftFootRotation, factor)
            rotateLeftFoot(interpolated, vertexdata)

            interpolated = getInterpolation(lastKeyFrame.rightFootRotation, nextKeyFrame.rightFootRotation, factor)
            rotateRightFoot(interpolated, vertexdata)

            interpolated =
                getInterpolation(lastKeyFrame.leftLowerLegRotation, nextKeyFrame.leftLowerLegRotation, factor)
            rotateLeftLowerLeg(interpolated, vertexdata)

            interpolated =
                getInterpolation(lastKeyFrame.rightLowerLegRotation, nextKeyFrame.rightLowerLegRotation, factor)
            rotateRightLowerLeg(interpolated, vertexdata)

            interpolated =
                getInterpolation(lastKeyFrame.leftUpperLegRotation, nextKeyFrame.leftUpperLegRotation, factor)
            rotateLeftUpperLeg(interpolated, vertexdata)

            interpolated =
                getInterpolation(lastKeyFrame.rightUpperLegRotation, nextKeyFrame.rightUpperLegRotation, factor)
            rotateRightUpperLeg(interpolated, vertexdata)

            //interpolated = getInterpolation(lastKeyFrame.rightHandRotation, nextKeyFrame.rightHandRotation, factor)
            //rotateLeftHand(interpolated, vertexdata)

            interpolated = getInterpolation(lastKeyFrame.rightHandRotation, nextKeyFrame.rightHandRotation, factor)
            rotateRightHand(interpolated, vertexdata)

            interpolated = getInterpolation(lastKeyFrame.leftHandRotation, nextKeyFrame.leftHandRotation, factor)
            rotateLeftHand(interpolated, vertexdata)

            interpolated =
                getInterpolation(lastKeyFrame.leftLowerArmRotation, nextKeyFrame.leftLowerArmRotation, factor)
            rotateLeftLowerArm(interpolated, vertexdata)

            interpolated =
                getInterpolation(lastKeyFrame.rightLowerArmRotation, nextKeyFrame.rightLowerArmRotation, factor)
            rotateRightLowerArm(interpolated, vertexdata)

            interpolated =
                getInterpolation(lastKeyFrame.leftUpperArmRotation, nextKeyFrame.leftUpperArmRotation, factor)
            rotateLeftUpperArm(interpolated, vertexdata)

            interpolated =
                getInterpolation(lastKeyFrame.rightUpperArmRotation, nextKeyFrame.rightUpperArmRotation, factor)
            rotateRightUpperArm(interpolated, vertexdata)

            interpolated = getInterpolation(lastKeyFrame.headrotation, nextKeyFrame.headrotation, factor)
            rotateHead(interpolated, vertexdata)

            //rotateRightLowerLeg(-45.0f, vertexdata)
            //rotateLeftLowerLeg(-45.0f, vertexdata)
            player!!.meshes[0].updateVertecies(vertexdata)
        }
        else if(passedTime != -1f)
        {
            passedTime = -1f
            var vertexdata: FloatArray = player!!.meshes[0].vertexdata.clone()
            rotateLeftFoot(0f, vertexdata)
            rotateRightFoot(0f, vertexdata)
            rotateLeftLowerLeg(0f, vertexdata)
            rotateRightLowerLeg(0f, vertexdata)
            rotateLeftUpperLeg(0f, vertexdata)
            rotateRightUpperLeg(0f, vertexdata)
            rotateRightHand(0f, vertexdata)
            rotateLeftHand(0f, vertexdata)
            rotateRightLowerArm(0f, vertexdata)
            rotateLeftUpperArm(0f, vertexdata)
            rotateRightUpperArm(0f, vertexdata)
            rotateHead(0f, vertexdata)

            //rotateRightLowerLeg(-45.0f, vertexdata)
            //rotateLeftLowerLeg(-45.0f, vertexdata)
            player!!.meshes[0].updateVertecies(vertexdata)
        }
    }
}