# CS210X_Project5_ExpressionEditor

# Important GUI Features #
* Terms can be drag-and-dropped only among siblings of the same parent in the expression tree
* Focus disappears if user clicks outside the expression tree and its immediate children
  * Clicking on an another expression while one is already selected, will just deselect the current expression selected
* Based on the (x,y) location of the mouse-click, the expression tree is searched from top to bottom for the first node that contains (x,y)
* Upon subsequent clicks, the focus may shift to one of the children of the current focus
* Create a shadow copy (ghost) that shows where the expression should be
 * Actual expression should follow the mouse
