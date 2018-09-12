part of backdrop;

class Backdrop extends StatefulWidget {
  final Widget backLayer;
  final Widget frontLayer;

  const Backdrop({
    Key key,
    @required this.backLayer,
    @required this.frontLayer,
  }) : super(key: key);

  @override
  _BackdropState createState() => _BackdropState(backLayer, frontLayer);
}

class _BackdropState extends State<Backdrop>
    with SingleTickerProviderStateMixin {
  /// The height of the back bar, matching the height of the app bar
  /// as per Material design specifications.
  ///
  /// https://material.io/design/components/backdrop.html#specs
  static const backBarHeight = 56.0;

  /// The remaining visible height of the front layer, when completely hidden.
  ///
  /// https://material.io/design/components/backdrop.html#specs
  static const hiddenFrontLayerHeight = 48.0;


  /// The border radius of the front layer.
  ///
  /// A bit larger than the specs define:
  /// https://material.io/design/components/backdrop.html#specs
  static const frontLayerBorderRadius =
      BorderRadius.vertical(top: Radius.circular(24.0));

  final Widget backLayer;
  final Widget frontLayer;
  AnimationController controller;

  @override
  void initState() {
    super.initState();

    controller = AnimationController(vsync: this)
      ..addListener(() {
        setState(() {});
      });
  }

  _BackdropState(this.backLayer, this.frontLayer);

  wrapInGestureDetectorIfClosed(
      final BoxConstraints constraints, final Widget child) {
    if (controller.status != AnimationStatus.dismissed) {
      return GestureDetector(
        onVerticalDragUpdate: (dragDetails) {
          setState(() {
            controller.value +=
                dragDetails.primaryDelta / (constraints.biggest.height - 100);
          });
        },
        onVerticalDragEnd: (dragDetails) {
          final flingVelocity = dragDetails.velocity.pixelsPerSecond.dy /
              constraints.biggest.height;
          if (flingVelocity < 0.2 &&
              flingVelocity >= 0.0 &&
              controller.value < 0.8) {
            controller.fling(velocity: -1.0);
          } else {
            controller.fling(velocity: flingVelocity);
          }
        },
        onTap: () {
          switch (controller.status) {
            case AnimationStatus.forward:
            case AnimationStatus.completed:
              controller.fling(velocity: -.01);
              break;
            default:
              break;
          }
        },
        child: child,
      );
    } else
      return child;
  }

  @override
  Widget build(BuildContext context) => LayoutBuilder(
        builder: (context, constraints) => Stack(
              fit: StackFit.expand,
              children: <Widget>[
                // Background:
                Material(
                  color: Theme.of(context).primaryColor,
                ),
                // Back Bar:
                Positioned(
                  top: 0.0,
                  height: backBarHeight,
                  left: 0.0,
                  right: 0.0,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: Stack(
                      children: <Widget>[
                        Positioned.fill(
                          child: Padding(
                            padding: const EdgeInsets.all(16.0),
                            child: Center(
                              child: Text(
                                "TESSERAPP",
                                style: Theme.of(context).primaryTextTheme.title,
                              ),
                            ),
                          ),
                        ),
                        Positioned.fill(
                          left: null,
                          child: FlatButton(
                            splashColor: Colors.transparent,
                            highlightColor: Colors.transparent,
                            onPressed: () {
                              switch (controller.status) {
                                case AnimationStatus.forward:
                                case AnimationStatus.completed:
                                  controller.fling(velocity: -.01);
                                  break;
                                case AnimationStatus.reverse:
                                case AnimationStatus.dismissed:
                                  controller.fling(velocity: 0.01);
                                  break;
                              }
                            },
                            child: Padding(
                              padding:
                                  const EdgeInsets.symmetric(vertical: 16.0),
                              child: AspectRatio(
                                aspectRatio: 1.0,
                                child: MorphingArrow(
                                  color: Theme.of(context)
                                      .primaryTextTheme
                                      .title
                                      .color,
                                  advance: controller.value,
                                ),
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                // Back layer:
                Positioned(
                  top: backBarHeight,
                  bottom: hiddenFrontLayerHeight,
                  left: 0.0,
                  right: 0.0,
                  child: backLayer,
                ),
                // Front layer:
                Positioned(
                  top: backBarHeight +
                      controller.value *
                          (constraints.biggest.height -
                              hiddenFrontLayerHeight -
                              backBarHeight),
                  height: constraints.biggest.height - backBarHeight,
                  left: 0.0,
                  right: 0.0,
                  child: Stack(
                    children: <Widget>[
                      Material(
                        borderRadius: frontLayerBorderRadius,
                        elevation: 4.0,
                        color: Theme.of(context).scaffoldBackgroundColor,
                        child: frontLayer,
                      ),
                      (controller.status != AnimationStatus.dismissed)
                          ? GestureDetector(
                              onVerticalDragUpdate: (dragDetails) {
                                setState(() {
                                  controller.value += dragDetails.primaryDelta /
                                      (constraints.biggest.height - 100);
                                });
                              },
                              onVerticalDragEnd: (dragDetails) {
                                final flingVelocity =
                                    dragDetails.velocity.pixelsPerSecond.dy /
                                        constraints.biggest.height;
                                if (flingVelocity < 0.2 &&
                                    flingVelocity >= 0.0 &&
                                    controller.value < 0.8) {
                                  controller.fling(velocity: -1.0);
                                } else {
                                  controller.fling(velocity: flingVelocity);
                                }
                              },
                              onTap: () {
                                switch (controller.status) {
                                  case AnimationStatus.forward:
                                  case AnimationStatus.completed:
                                    controller.fling(velocity: -.01);
                                    break;
                                  default:
                                    break;
                                }
                              },
                            )
                          : null
                    ].where((widget) => widget != null).toList(),
                  ),
                ),
              ],
            ),
      );

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}
