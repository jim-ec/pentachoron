
import 'package:flutter/material.dart';

class Button extends StatelessWidget {
  
  final Widget child;
  final VoidCallback onPressed;
  
  const Button({Key key, this.child, this.onPressed}) : super(key: key);
  
  @override
  Widget build(BuildContext context) => OutlineButton(
    highlightElevation: 0.0,
    child: child,
    color: Theme.of(context).toggleableActiveColor,
    borderSide: BorderSide(
        width: 0.5,
        color: Theme.of(context).primaryTextTheme.button.color
    ),
    shape: new RoundedRectangleBorder(
        borderRadius: new BorderRadius.circular(30.0)),
    onPressed: onPressed,
  );
  
}

