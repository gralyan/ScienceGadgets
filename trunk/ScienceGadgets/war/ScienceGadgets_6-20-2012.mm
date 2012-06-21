<map version="0.9.0">
<!--To view this file, download free mind mapping software Freeplane from http://freeplane.sourceforge.net -->
<node TEXT="ScienceGadgets&#xa;source code" BACKGROUND_COLOR="#00ff00">
<hook NAME="MapStyle" max_node_width="600"/>


<node TEXT="" BACKGROUND_COLOR="#eee8aa">
</node>

<node TEXT="com" BACKGROUND_COLOR="#eee8aa">
</node>

<node TEXT="com.sciencegadgets" BACKGROUND_COLOR="#eee8aa">
</node>

<node TEXT="com.sciencegadgets.client" BACKGROUND_COLOR="#eee8aa">

<node TEXT="ScienceGadgets.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private final GreetingServiceAsync greetingService = GWT&#xa;            .create(GreetingService.class);" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void onModuleLoad()" ID="Lcom/sciencegadgets/client/ScienceGadgets;.onModuleLoad()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void string2MathML_BySymja_OnServer(String textToServer)" ID="Lcom/sciencegadgets/client/ScienceGadgets;.string2MathML_BySymja_OnServer(QString;)V" BACKGROUND_COLOR="#ffa07a">
</node>
</node>

<node TEXT="GreetingServiceAsync.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="void greetServer(String input, AsyncCallback&lt;String&gt; callback)" ID="Lcom/sciencegadgets/client/GreetingServiceAsync;.greetServer(QString;QAsyncCallback&lt;QString;&gt;;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/ScienceGadgets;.string2MathML_BySymja_OnServer(Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="string2MathML_BySymja_OnServer" TARGET_LABEL="greetServer" MIDDLE_LABEL="greetServer"/>
</node>
</node>

<node TEXT="GreetingService.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="String greetServer(String name)" ID="Lcom/sciencegadgets/client/GreetingService;.greetServer(QString;)V" BACKGROUND_COLOR="#ffa07a">
</node>
</node>

<node TEXT="Log.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private static Logger log = Logger.getLogger(&quot;logger&quot;);" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void info(String msg)" ID="Lcom/sciencegadgets/client/Log;.info(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNestedMrows()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNestedMrows" TARGET_LABEL="info" MIDDLE_LABEL="info"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(Lcom/google/gwt/user/client/ui/HTML;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="transport" TARGET_LABEL="info" MIDDLE_LABEL="info"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="info" MIDDLE_LABEL="info"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="info" MIDDLE_LABEL="info"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.selectEquation(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="selectEquation" TARGET_LABEL="info" MIDDLE_LABEL="info"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="info" MIDDLE_LABEL="info"/>
</node>

<node TEXT="void severe(String msg)" ID="Lcom/sciencegadgets/client/Log;.severe(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/AlgebraManipulator;.makeWrappers(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="makeWrappers" TARGET_LABEL="severe" MIDDLE_LABEL="severe"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.registerDropController(Lcom/allen_sauer/gwt/dnd/client/drop/DropController;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="registerDropController" TARGET_LABEL="severe" MIDDLE_LABEL="severe"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(Lcom/google/gwt/user/client/ui/HTML;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="transport" TARGET_LABEL="severe" MIDDLE_LABEL="severe"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.selectEquation(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="selectEquation" TARGET_LABEL="severe" MIDDLE_LABEL="severe"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="severe" MIDDLE_LABEL="severe"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="severe" MIDDLE_LABEL="severe"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="severe" MIDDLE_LABEL="severe"/>
</node>
</node>

<node TEXT="DragDropTesting.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private PickupDragController dragC1, dragC2, dragC3, dragC4;&#xa;private PickupDragController dragC1, dragC2, dragC3, dragC4;&#xa;private PickupDragController dragC1, dragC2, dragC3, dragC4;&#xa;private PickupDragController dragC1, dragC2, dragC3, dragC4;&#xa;private HTML box1, box2, box3, box4;&#xa;private HTML box1, box2, box3, box4;&#xa;private HTML box1, box2, box3, box4;&#xa;private HTML box1, box2, box3, box4;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void onModuleLoad()" ID="Lcom/sciencegadgets/client/DragDropTesting;.onModuleLoad()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void registerAll()" ID="Lcom/sciencegadgets/client/DragDropTesting;.registerAll()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/DragDropTesting$ElementDrop;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="registerAll" MIDDLE_LABEL="registerAll"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/DragDropTesting;.onModuleLoad()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onModuleLoad" TARGET_LABEL="registerAll" MIDDLE_LABEL="registerAll"/>
</node>

<node TEXT="void onMouseOver(MouseOverEvent event)" ID="Lcom/sciencegadgets/client/DragDropTesting$ElementOverHandler;.onMouseOver(QMouseOverEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/MouseOverEvent;.dispatch(Lcom/google/gwt/event/dom/client/MouseOverHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onMouseOver" MIDDLE_LABEL="onMouseOver"/>
</node>

<node TEXT="void onMouseOut(MouseOutEvent event)" ID="Lcom/sciencegadgets/client/DragDropTesting$ElementOutHandler;.onMouseOut(QMouseOutEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/MouseOutEvent;.dispatch(Lcom/google/gwt/event/dom/client/MouseOutHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onMouseOut" MIDDLE_LABEL="onMouseOut"/>
</node>

<node TEXT="void ElementDrop(Widget dropTarget)" ID="Lcom/sciencegadgets/client/DragDropTesting$ElementDrop;.ElementDrop(QWidget;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/DragDropTesting;.registerAll()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="registerAll" TARGET_LABEL="ElementDrop" MIDDLE_LABEL="ElementDrop"/>
</node>

<node TEXT="void onDrop(DragContext context)" ID="Lcom/sciencegadgets/client/DragDropTesting$ElementDrop;.onDrop(QDragContext;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/PickupDragController;.dragEnd()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragEnd" TARGET_LABEL="onDrop" MIDDLE_LABEL="onDrop"/>
</node>

<node TEXT="void onEnter(DragContext context)" ID="Lcom/sciencegadgets/client/DragDropTesting$ElementDrop;.onEnter(QDragContext;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/PickupDragController;.dragMove()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragMove" TARGET_LABEL="onEnter" MIDDLE_LABEL="onEnter"/>
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/PickupDragController;.dragStart()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragStart" TARGET_LABEL="onEnter" MIDDLE_LABEL="onEnter"/>
</node>
</node>

<node TEXT="TopNodesNotFoundException.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="/**&#xa;     * &#xa;     */&#xa;    private static final long serialVersionUID = -4468108184733068758L;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void TopNodesNotFoundException()" ID="Lcom/sciencegadgets/client/TopNodesNotFoundException;.TopNodesNotFoundException()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void TopNodesNotFoundException(String s)" ID="Lcom/sciencegadgets/client/TopNodesNotFoundException;.TopNodesNotFoundException(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLParser;.(Lcom/google/gwt/user/client/ui/HTML;)V|Lcom/sciencegadgets/client/TopNodesNotFoundException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathMLParser" TARGET_LABEL="TopNodesNotFoundException" MIDDLE_LABEL="TopNodesNotFoundException"/>
</node>
</node>

<node TEXT="NavigationPanelEntry.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private static final List&lt;String&gt; navigationIcons = Arrays.asList(&quot;About&quot;, &quot;Get ScienceGadgets&quot;, &quot;Roadmap&quot;);" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void onModuleLoad()" ID="Lcom/sciencegadgets/client/NavigationPanelEntry;.onModuleLoad()V" BACKGROUND_COLOR="#ffa07a">
</node>
</node>

<node TEXT="TextBoxEntry.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="TextBox mlInput;&#xa;TextBox jqInput;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void onModuleLoad()" ID="Lcom/sciencegadgets/client/TextBoxEntry;.onModuleLoad()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry$BackButtonHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="onModuleLoad" MIDDLE_LABEL="onModuleLoad"/>
</node>

<node TEXT="ml&#xa;jq" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="InputTypes inputType;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void InputHandler(InputTypes inputType)" ID="Lcom/sciencegadgets/client/TextBoxEntry$InputHandler;.InputHandler(QInputTypes;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/TextBoxEntry;.onModuleLoad()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onModuleLoad" TARGET_LABEL="InputHandler" MIDDLE_LABEL="InputHandler"/>
</node>

<node TEXT="void onClick(ClickEvent arg0)" ID="Lcom/sciencegadgets/client/TextBoxEntry$InputHandler;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>
</node>
</node>

<node TEXT="com.sciencegadgets.client.algebramanipulation" BACKGROUND_COLOR="#eee8aa">

<node TEXT="AlgebraManipulator.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="HTML draggableEquation;&#xa;AbsolutePanel parentPanel;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void AlgebraManipulator(HTML draggableEquation, LinkedList&lt;MLElementWrapper&gt; wrappers, AbsolutePanel parentPanel)" ID="Lcom/sciencegadgets/client/algebramanipulation/AlgebraManipulator;.AlgebraManipulator(QHTML;QLinkedList&lt;QMLElementWrapper;&gt;;QAbsolutePanel;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry;.updateAlgOut(Lcom/google/gwt/user/client/ui/HTML;Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="updateAlgOut" TARGET_LABEL="AlgebraManipulator" MIDDLE_LABEL="AlgebraManipulator"/>
</node>

<node TEXT="void makeWrappers(LinkedList&lt;MLElementWrapper&gt; wrappers)" ID="Lcom/sciencegadgets/client/algebramanipulation/AlgebraManipulator;.makeWrappers(QLinkedList&lt;QMLElementWrapper;&gt;;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/AlgebraManipulator;.(Lcom/google/gwt/user/client/ui/HTML;Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Lcom/google/gwt/user/client/ui/AbsolutePanel;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="AlgebraManipulator" TARGET_LABEL="makeWrappers" MIDDLE_LABEL="makeWrappers"/>
</node>
</node>

<node TEXT="WrapDragController.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="// private ArrayList&lt;DropController&gt; dropList;&#xa;    // private ArrayList&lt;MLElementWrapper&gt; dropWrapperList;&#xa;    private Map&lt;DropController, MLElementWrapper&gt; dropMap;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void WrapDragController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel)" ID="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.WrapDragController(QAbsolutePanel;Z)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.addDragController()Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDragController" TARGET_LABEL="WrapDragController" MIDDLE_LABEL="WrapDragController"/>
</node>

<node TEXT="void dragStart()" ID="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.dragStart()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/MouseDragHandler;.startDragging()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="startDragging" TARGET_LABEL="dragStart" MIDDLE_LABEL="dragStart"/>
</node>

<node TEXT="void dragEnd()" ID="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.dragEnd()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/MouseDragHandler;.drop(II)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drop" TARGET_LABEL="dragEnd" MIDDLE_LABEL="dragEnd"/>
</node>

<node TEXT="Set&lt;DropController&gt; getDropList()" ID="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.getDropList()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.removeDropTargets()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="removeDropTargets" TARGET_LABEL="getDropList" MIDDLE_LABEL="getDropList"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="getDropList" MIDDLE_LABEL="getDropList"/>
</node>

<node TEXT="Collection&lt;MLElementWrapper&gt; getDropWrapList()" ID="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.getDropWrapList()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void registerDropController(DropController dropController)" ID="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.registerDropController(QDropController;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/PickupDragController;.(Lcom/google/gwt/user/client/ui/AbsolutePanel;Z)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="PickupDragController" TARGET_LABEL="registerDropController" MIDDLE_LABEL="registerDropController"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/DragDropTesting;.registerAll()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="registerAll" TARGET_LABEL="registerDropController" MIDDLE_LABEL="registerDropController"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="registerDropController" MIDDLE_LABEL="registerDropController"/>
</node>

<node TEXT="void unregisterDropController(DropController dropController)" ID="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.unregisterDropController(QDropController;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void unregisterDropControllers()" ID="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.unregisterDropControllers()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/DragDropTesting$ElementDrop;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="unregisterDropControllers" MIDDLE_LABEL="unregisterDropControllers"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.removeDropTargets()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="removeDropTargets" TARGET_LABEL="unregisterDropControllers" MIDDLE_LABEL="unregisterDropControllers"/>
</node>
</node>

<node TEXT="MLElementWrapper.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private WrapDragController dragController = null;&#xa;private Element element = null;&#xa;private Boolean isDraggable;&#xa;private MLElementWrapper joinedWrapper;&#xa;private MathMLBindingNode mathMLBindingNode;&#xa;private HTML dropDescriptor = new HTML();&#xa;private static MLElementWrapper selectedWrapper;&#xa;private static MLElementWrapper SelectedWrapperJoiner;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void MLElementWrapper(MathMLBindingNode jNode, Boolean isDraggable, Boolean isJoined)" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.MLElementWrapper(QMathMLBindingNode;QBoolean;QBoolean;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.wrapChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="wrapChildren" TARGET_LABEL="MLElementWrapper" MIDDLE_LABEL="MLElementWrapper"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Ljava/lang/Boolean;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MLElementWrapper" TARGET_LABEL="MLElementWrapper" MIDDLE_LABEL="MLElementWrapper"/>
</node>

<node TEXT="HTML getDropDescriptor()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.getDropDescriptor()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.addFirstLayer(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)[I" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addFirstLayer" TARGET_LABEL="getDropDescriptor" MIDDLE_LABEL="getDropDescriptor"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="getDropDescriptor" MIDDLE_LABEL="getDropDescriptor"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="getDropDescriptor" MIDDLE_LABEL="getDropDescriptor"/>
</node>

<node TEXT="Element getElementWrapped()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.getElementWrapped()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getElementWrapped" MIDDLE_LABEL="getElementWrapped"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/AlgebraManipulator;.makeWrappers(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="makeWrappers" TARGET_LABEL="getElementWrapped" MIDDLE_LABEL="getElementWrapped"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getElementWrapped" MIDDLE_LABEL="getElementWrapped"/>
</node>

<node TEXT="void setElementWrapped(Element el)" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.setElementWrapped(QElement;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="MLElementWrapper getJoinedWrapper()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.getJoinedWrapper()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.addFirstLayer(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)[I" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addFirstLayer" TARGET_LABEL="getJoinedWrapper" MIDDLE_LABEL="getJoinedWrapper"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="getJoinedWrapper" MIDDLE_LABEL="getJoinedWrapper"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="getJoinedWrapper" MIDDLE_LABEL="getJoinedWrapper"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="getJoinedWrapper" MIDDLE_LABEL="getJoinedWrapper"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="getJoinedWrapper" MIDDLE_LABEL="getJoinedWrapper"/>
</node>

<node TEXT="MathMLBindingTree.MathMLBindingNode getJohnNode()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.getJohnNode()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.dragStart()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragStart" TARGET_LABEL="getJohnNode" MIDDLE_LABEL="getJohnNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="getJohnNode" MIDDLE_LABEL="getJohnNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="getJohnNode" MIDDLE_LABEL="getJohnNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.(Lcom/google/gwt/user/client/ui/Widget;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="AbstractMathDropController" TARGET_LABEL="getJohnNode" MIDDLE_LABEL="getJohnNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="getJohnNode" MIDDLE_LABEL="getJohnNode"/>
</node>

<node TEXT="WrapDragController getDragControl()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.getDragControl()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="getDragControl" MIDDLE_LABEL="getDragControl"/>
</node>

<node TEXT="void onAttach()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.onAttach()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/DialogBox;.doAttachChildren()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="doAttachChildren" TARGET_LABEL="onAttach" MIDDLE_LABEL="onAttach"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/AttachDetachException$1027;.execute(Lcom/google/gwt/user/client/ui/Widget;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="execute" TARGET_LABEL="onAttach" MIDDLE_LABEL="onAttach"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/Label;.wrap(Lcom/google/gwt/dom/client/Element;)Lcom/google/gwt/user/client/ui/Label;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="wrap" TARGET_LABEL="onAttach" MIDDLE_LABEL="onAttach"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/Widget;.setParent(Lcom/google/gwt/user/client/ui/Widget;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="setParent" TARGET_LABEL="onAttach" MIDDLE_LABEL="onAttach"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/Composite;.onAttach()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onAttach" TARGET_LABEL="onAttach" MIDDLE_LABEL="onAttach"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/HTML;.wrap(Lcom/google/gwt/dom/client/Element;)Lcom/google/gwt/user/client/ui/HTML;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="wrap" TARGET_LABEL="onAttach" MIDDLE_LABEL="onAttach"/>
</node>

<node TEXT="HandlerRegistration addMouseOverHandler()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.addMouseOverHandler()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Ljava/lang/Boolean;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MLElementWrapper" TARGET_LABEL="addMouseOverHandler" MIDDLE_LABEL="addMouseOverHandler"/>
</node>

<node TEXT="HandlerRegistration addMouseOutHandler()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.addMouseOutHandler()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Ljava/lang/Boolean;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MLElementWrapper" TARGET_LABEL="addMouseOutHandler" MIDDLE_LABEL="addMouseOutHandler"/>
</node>

<node TEXT="HandlerRegistration addClickHandler()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.addClickHandler()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Ljava/lang/Boolean;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MLElementWrapper" TARGET_LABEL="addClickHandler" MIDDLE_LABEL="addClickHandler"/>
</node>

<node TEXT="WrapDragController addDragController()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.addDragController()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.onAttach()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onAttach" TARGET_LABEL="addDragController" MIDDLE_LABEL="addDragController"/>
</node>

<node TEXT="void removeDragController()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.removeDragController()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void removeDropTargets()" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.removeDropTargets()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void select(Boolean select)" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(QBoolean;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper$ElementClickHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="select" MIDDLE_LABEL="select"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper$ElementOverHandler;.onMouseOver(Lcom/google/gwt/event/dom/client/MouseOverEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onMouseOver" TARGET_LABEL="select" MIDDLE_LABEL="select"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper$ElementOutHandler;.onMouseOut(Lcom/google/gwt/event/dom/client/MouseOutEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onMouseOut" TARGET_LABEL="select" MIDDLE_LABEL="select"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.dragEnd()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragEnd" TARGET_LABEL="select" MIDDLE_LABEL="select"/>
</node>

<node TEXT="void onMouseOver(MouseOverEvent event)" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper$ElementOverHandler;.onMouseOver(QMouseOverEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/MouseOverEvent;.dispatch(Lcom/google/gwt/event/dom/client/MouseOverHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onMouseOver" MIDDLE_LABEL="onMouseOver"/>
</node>

<node TEXT="void onMouseOut(MouseOutEvent event)" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper$ElementOutHandler;.onMouseOut(QMouseOutEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/MouseOutEvent;.dispatch(Lcom/google/gwt/event/dom/client/MouseOutHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onMouseOut" MIDDLE_LABEL="onMouseOut"/>
</node>

<node TEXT="void onClick(ClickEvent event)" ID="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper$ElementClickHandler;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>
</node>

<node TEXT="EquationTransporter.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="public static TreeCanvas tCanvas;&#xa;private static DropControllAssigner dropAssigner;&#xa;public static LinkedList&lt;AbstractMathDropController&gt; dropControllers;&#xa;private static MathMLBindingTree jTree;&#xa;public static TreeCanvas mltCanvas;&#xa;private static MathMLBindingTree mljTree;&#xa;private static AbsolutePanel treePanel;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="String transport(String jqMath)" ID="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/TextBoxEntry$InputHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="transport" MIDDLE_LABEL="transport"/>
</node>

<node TEXT="void transport(HTML mathML)" ID="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(QHTML;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/TextBoxEntry$InputHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="transport" MIDDLE_LABEL="transport"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(Ljava/lang/String;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="transport" TARGET_LABEL="transport" MIDDLE_LABEL="transport"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.sendAlgebraEquation(Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="sendAlgebraEquation" TARGET_LABEL="transport" MIDDLE_LABEL="transport"/>
</node>

<node TEXT="void selectEquation(HTML mathML, String changeComment)" ID="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.selectEquation(QHTML;QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(Lcom/google/gwt/user/client/ui/HTML;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="transport" TARGET_LABEL="selectEquation" MIDDLE_LABEL="selectEquation"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="selectEquation" MIDDLE_LABEL="selectEquation"/>
</node>

<node TEXT="void parseJQMath(Element element)" ID="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.parseJQMath(QElement;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(Ljava/lang/String;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="transport" TARGET_LABEL="parseJQMath" MIDDLE_LABEL="parseJQMath"/>
</node>
</node>

<node TEXT="AlgOutEntry.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="public static Grid algOut = new Grid(0, 1);&#xa;public static AbsolutePanel wrapperPanel = new AbsolutePanel();&#xa;public static ListBox varBox;&#xa;public static ListBox funBox;&#xa;public static TextBox coefBox;&#xa;public static ScrollPanel spAlg = new ScrollPanel(algOut);&#xa;private static HTML prevEquation;&#xa;private Button backButton = new Button(&quot;Back&quot;, new BackButtonHandler());&#xa;EquationDatabase data;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void onModuleLoad()" ID="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry;.onModuleLoad()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(Lcom/google/gwt/user/client/ui/HTML;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="transport" TARGET_LABEL="onModuleLoad" MIDDLE_LABEL="onModuleLoad"/>
</node>

<node TEXT="void createAlgebraPanel()" ID="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry;.createAlgebraPanel()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry;.onModuleLoad()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onModuleLoad" TARGET_LABEL="createAlgebraPanel" MIDDLE_LABEL="createAlgebraPanel"/>
</node>

<node TEXT="void updateAlgOut(HTML mathML, LinkedList&lt;MLElementWrapper&gt; wrappers, String change)" ID="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry;.updateAlgOut(QHTML;QLinkedList&lt;QMLElementWrapper;&gt;;QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry$ToBothSidesHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="updateAlgOut" MIDDLE_LABEL="updateAlgOut"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.selectEquation(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="selectEquation" TARGET_LABEL="updateAlgOut" MIDDLE_LABEL="updateAlgOut"/>
</node>

<node TEXT="void onClick(ClickEvent arg0)" ID="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry$BackButtonHandler;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>

<node TEXT="void onClick(ClickEvent event)" ID="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry$ToBothSidesHandler;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>
</node>
</node>

<node TEXT="com.sciencegadgets.client.algebramanipulation.dropcontrollers" BACKGROUND_COLOR="#eee8aa">

<node TEXT="DropController_BothSides_Multiply.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="void DropController_BothSides_Multiply(Widget dropTarget)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.DropController_BothSides_Multiply(QWidget;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="DropController_BothSides_Multiply" MIDDLE_LABEL="DropController_BothSides_Multiply"/>
</node>

<node TEXT="void onChange()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="onChange" MIDDLE_LABEL="onChange"/>
</node>

<node TEXT="String findChange(MathMLBindingNode sourceNode)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.findChange(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
</node>

<node TEXT="String changeComment()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.changeComment()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="changeComment" MIDDLE_LABEL="changeComment"/>
</node>
</node>

<node TEXT="DropController_BothSides_Divide.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="void DropController_BothSides_Divide(Widget dropTarget)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.DropController_BothSides_Divide(QWidget;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="DropController_BothSides_Divide" MIDDLE_LABEL="DropController_BothSides_Divide"/>
</node>

<node TEXT="void onChange()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="onChange" MIDDLE_LABEL="onChange"/>
</node>

<node TEXT="String findChange(MathMLBindingNode sourceNode)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.findChange(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
</node>

<node TEXT="String changeComment()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.changeComment()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="changeComment" MIDDLE_LABEL="changeComment"/>
</node>
</node>

<node TEXT="DropController_Simplify_Multiply.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private float answer;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void DropController_Simplify_Multiply(Widget dropTarget)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.DropController_Simplify_Multiply(QWidget;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="DropController_Simplify_Multiply" MIDDLE_LABEL="DropController_Simplify_Multiply"/>
</node>

<node TEXT="void onChange()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="onChange" MIDDLE_LABEL="onChange"/>
</node>

<node TEXT="String findChange(MathMLBindingNode sourceNode)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.findChange(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
</node>

<node TEXT="String changeComment()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.changeComment()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="changeComment" MIDDLE_LABEL="changeComment"/>
</node>
</node>

<node TEXT="DropController_Simplify_Add.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private float answer;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void DropController_Simplify_Add(Widget dropTarget)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.DropController_Simplify_Add(QWidget;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="DropController_Simplify_Add" MIDDLE_LABEL="DropController_Simplify_Add"/>
</node>

<node TEXT="void onChange()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="onChange" MIDDLE_LABEL="onChange"/>
</node>

<node TEXT="String findChange(MathMLBindingNode sourceNode)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.findChange(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
</node>

<node TEXT="String changeComment()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.changeComment()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="changeComment" MIDDLE_LABEL="changeComment"/>
</node>
</node>

<node TEXT="AbstractMathDropController.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="protected MLElementWrapper source;&#xa;protected MLElementWrapper target;&#xa;protected MathMLBindingNode sourceNode;&#xa;protected MathMLBindingNode targetNode;&#xa;public String change = &quot;&quot;;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void AbstractMathDropController(Widget dropTarget)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.AbstractMathDropController(QWidget;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.(Lcom/google/gwt/user/client/ui/Widget;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="DropController_Simplify_Multiply" TARGET_LABEL="AbstractMathDropController" MIDDLE_LABEL="AbstractMathDropController"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.(Lcom/google/gwt/user/client/ui/Widget;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="DropController_BothSides_Add" TARGET_LABEL="AbstractMathDropController" MIDDLE_LABEL="AbstractMathDropController"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.(Lcom/google/gwt/user/client/ui/Widget;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="DropController_BothSides_Multiply" TARGET_LABEL="AbstractMathDropController" MIDDLE_LABEL="AbstractMathDropController"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.(Lcom/google/gwt/user/client/ui/Widget;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="DropController_Simplify_Add" TARGET_LABEL="AbstractMathDropController" MIDDLE_LABEL="AbstractMathDropController"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.(Lcom/google/gwt/user/client/ui/Widget;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="DropController_BothSides_Divide" TARGET_LABEL="AbstractMathDropController" MIDDLE_LABEL="AbstractMathDropController"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.(Lcom/google/gwt/user/client/ui/Widget;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="DropController_Simplify_Divide" TARGET_LABEL="AbstractMathDropController" MIDDLE_LABEL="AbstractMathDropController"/>
</node>

<node TEXT="void onDrop(DragContext context)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(QDragContext;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/PickupDragController;.dragEnd()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragEnd" TARGET_LABEL="onDrop" MIDDLE_LABEL="onDrop"/>
</node>

<node TEXT="void onEnter(DragContext context)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onEnter(QDragContext;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/PickupDragController;.dragMove()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragMove" TARGET_LABEL="onEnter" MIDDLE_LABEL="onEnter"/>
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/PickupDragController;.dragStart()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragStart" TARGET_LABEL="onEnter" MIDDLE_LABEL="onEnter"/>
</node>

<node TEXT="void onLeave(DragContext context)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onLeave(QDragContext;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/PickupDragController;.dragMove()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragMove" TARGET_LABEL="onLeave" MIDDLE_LABEL="onLeave"/>
<arrowlink DESTINATION="Lcom/allen_sauer/gwt/dnd/client/PickupDragController;.dragEnd()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragEnd" TARGET_LABEL="onLeave" MIDDLE_LABEL="onLeave"/>
</node>

<node TEXT="void onChange()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onChange()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="onChange" MIDDLE_LABEL="onChange"/>
</node>

<node TEXT="String findChange(MathMLBindingNode sourceNode)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.findChange(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
</node>

<node TEXT="String changeComment()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.changeComment()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="changeComment" MIDDLE_LABEL="changeComment"/>
</node>
</node>

<node TEXT="DropController_BothSides_Add.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="void DropController_BothSides_Add(Widget dropTarget)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.DropController_BothSides_Add(QWidget;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="DropController_BothSides_Add" MIDDLE_LABEL="DropController_BothSides_Add"/>
</node>

<node TEXT="void onChange()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="onChange" MIDDLE_LABEL="onChange"/>
</node>

<node TEXT="String findChange(MathMLBindingNode sourceNode)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.findChange(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
</node>

<node TEXT="String changeComment()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.changeComment()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="changeComment" MIDDLE_LABEL="changeComment"/>
</node>
</node>

<node TEXT="DropController_Simplify_Divide.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private float answer;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void DropController_Simplify_Divide(Widget dropTarget)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.DropController_Simplify_Divide(QWidget;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="DropController_Simplify_Divide" MIDDLE_LABEL="DropController_Simplify_Divide"/>
</node>

<node TEXT="void onChange()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.onChange()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="onChange" MIDDLE_LABEL="onChange"/>
</node>

<node TEXT="String findChange(MathMLBindingNode sourceNode)" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.findChange(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.select(Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="select" TARGET_LABEL="findChange" MIDDLE_LABEL="findChange"/>
</node>

<node TEXT="String changeComment()" ID="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.changeComment()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="changeComment" MIDDLE_LABEL="changeComment"/>
</node>
</node>
</node>

<node TEXT="com.sciencegadgets.client.equationbrowser" BACKGROUND_COLOR="#eee8aa">

<node TEXT="EquationDatabase.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="// flags for methods&#xa;    public final byte FLAG_EQUATION = 1;&#xa;public final byte FLAG_VARIABLE = 2;&#xa;public final byte FLAG_QUANTITY = 3;&#xa;public final byte FLAG_UNIT = 4;&#xa;public final byte FLAG_ALGEBRA = 5;&#xa;public final byte[] FLAG_EQUATION_JQMATH = { FLAG_EQUATION, 0 };&#xa;public final byte[] FLAG_EQUATION_ML = { FLAG_EQUATION, 1 };&#xa;public final byte[] FLAG_VARIABLE_LOOK = { FLAG_VARIABLE, 0 };&#xa;public final byte[] FLAG_VARIABLE_DESCRIPTION = { FLAG_VARIABLE, 1 };&#xa;public final byte[] FLAG_ALGEBRA_NAME = { FLAG_ALGEBRA, 0 };&#xa;public final byte[] FLAG_ALGEBRA_EQUATION = { FLAG_ALGEBRA, 1 };&#xa;// tables of one-to-one relationships&#xa;&#xa;    // 0-equation 1-description 2&#xa;    private final String[][] EQUATIONS = {&#xa;            // Newtonian Mechanics&#xa;&#xa;            /* 0 */{&#xa;                    &quot;ν=ν_0 + at&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;ν=ν_0 + at\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;ν&lt;/mi&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;ν&lt;/mi&gt;&lt;mn&gt;0&lt;/mn&gt;&lt;/msub&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mi&gt;t&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 1 */{&#xa;                    &quot;x=x_0 + ν_0t + 1/2at^2&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;x=x_0 + ν_0t + 1/2at^2\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;x&lt;/mi&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;x&lt;/mi&gt;&lt;mn&gt;0&lt;/mn&gt;&lt;/msub&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;ν&lt;/mi&gt;&lt;mn&gt;0&lt;/mn&gt;&lt;/msub&gt;&lt;mi&gt;t&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mfrac&gt;&lt;mn&gt;1&lt;/mn&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/mfrac&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;/mrow&gt;&lt;msup&gt;&lt;mi&gt;t&lt;/mi&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/msup&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 2 */{&#xa;                    &quot;ν^2=ν_0^2 + 2a(x - x_0)&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;ν^2=ν_0^2 + 2a(x - x_0)\&quot;&gt;&lt;mrow&gt;&lt;msup&gt;&lt;mi&gt;ν&lt;/mi&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/msup&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;msubsup&gt;&lt;mi&gt;ν&lt;/mi&gt;&lt;mn&gt;0&lt;/mn&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/msubsup&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;/mrow&gt;&lt;mrow&gt;&lt;mo&gt;(&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;x&lt;/mi&gt;&lt;mo&gt;−&lt;/mo&gt;&lt;msub&gt;&lt;mi&gt;x&lt;/mi&gt;&lt;mn&gt;0&lt;/mn&gt;&lt;/msub&gt;&lt;/mrow&gt;&lt;mo&gt;)&lt;/mo&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 3 */{&#xa;                    &quot;F_\\net = ma&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;F_\\net = ma\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;F&lt;/mi&gt;&lt;mi class=\&quot;ma-repel-adj\&quot;&gt;net&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;m&lt;/mi&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 4 */{&#xa;                    &quot;F_\\fric ≤ μN&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;F_\\fric ≤ μN\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;F&lt;/mi&gt;&lt;mi class=\&quot;ma-repel-adj\&quot;&gt;fric&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;≤&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;μ&lt;/mi&gt;&lt;mi&gt;N&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 5 */{&#xa;                    &quot;a_c = ν^2/r&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;a_c = ν^2/r\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mi&gt;c&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mfrac&gt;&lt;msup&gt;&lt;mi&gt;ν&lt;/mi&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/msup&gt;&lt;mi&gt;r&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 6 */{&#xa;                    &quot;τ=rF{\\sinθ}&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;τ=rF{\\sinθ}\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;τ&lt;/mi&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow class=\&quot;ma-repel-adj\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;r&lt;/mi&gt;&lt;mi&gt;F&lt;/mi&gt;&lt;/mrow&gt;&lt;mspace width=\&quot;.17em\&quot;&gt;&lt;/mspace&gt;&lt;mrow class=\&quot;ma-repel-adj\&quot;&gt;&lt;mi class=\&quot;ma-repel-adj\&quot;&gt;sin&lt;/mi&gt;&lt;mspace width=\&quot;.17em\&quot;&gt;&lt;/mspace&gt;&lt;mi&gt;θ&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 7 */{&#xa;                    &quot;p=mν&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;p=mν\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;p&lt;/mi&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;m&lt;/mi&gt;&lt;mi&gt;ν&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 8 */{&#xa;                    &quot;J=F{Δt}&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;J=F{Δt}\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;J&lt;/mi&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;F&lt;/mi&gt;&lt;mrow&gt;&lt;mi&gt;Δ&lt;/mi&gt;&lt;mi&gt;t&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 9 */{&#xa;                    &quot;K=1/2mν^2&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;K=1/2mν^2\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;K&lt;/mi&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mfrac&gt;&lt;mn&gt;1&lt;/mn&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/mfrac&gt;&lt;mi&gt;m&lt;/mi&gt;&lt;/mrow&gt;&lt;msup&gt;&lt;mi&gt;ν&lt;/mi&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/msup&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 10 */{&#xa;                    &quot;\\U_g=mgh&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;\\U_g=mgh\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi class=\&quot;ma-upright\&quot; mathvariant=\&quot;normal\&quot;&gt;U&lt;/mi&gt;&lt;mi&gt;g&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;m&lt;/mi&gt;&lt;mi&gt;g&lt;/mi&gt;&lt;/mrow&gt;&lt;mi&gt;h&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 11 */{&#xa;                    &quot;W=F{Δr}{\\cosθ}&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;W=F{Δr}{\\cosθ}\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;W&lt;/mi&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow class=\&quot;ma-repel-adj\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;F&lt;/mi&gt;&lt;mrow&gt;&lt;mi&gt;Δ&lt;/mi&gt;&lt;mi&gt;r&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;mspace width=\&quot;.17em\&quot;&gt;&lt;/mspace&gt;&lt;mrow class=\&quot;ma-repel-adj\&quot;&gt;&lt;mi class=\&quot;ma-repel-adj\&quot;&gt;cos&lt;/mi&gt;&lt;mspace width=\&quot;.17em\&quot;&gt;&lt;/mspace&gt;&lt;mi&gt;θ&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 12 */{&#xa;                    &quot;P_\\avg=W/{Δt}&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;P_\\avg=W/{Δt}\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;P&lt;/mi&gt;&lt;mi class=\&quot;ma-repel-adj\&quot;&gt;avg&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mfrac&gt;&lt;mi&gt;W&lt;/mi&gt;&lt;mrow&gt;&lt;mi&gt;Δ&lt;/mi&gt;&lt;mi&gt;t&lt;/mi&gt;&lt;/mrow&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 13 */{&#xa;                    &quot;P=Fν{\\cosθ}&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;P=Fν{\\cosθ}\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;P&lt;/mi&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow class=\&quot;ma-repel-adj\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;F&lt;/mi&gt;&lt;mi&gt;ν&lt;/mi&gt;&lt;/mrow&gt;&lt;mspace width=\&quot;.17em\&quot;&gt;&lt;/mspace&gt;&lt;mrow class=\&quot;ma-repel-adj\&quot;&gt;&lt;mi class=\&quot;ma-repel-adj\&quot;&gt;cos&lt;/mi&gt;&lt;mspace width=\&quot;.17em\&quot;&gt;&lt;/mspace&gt;&lt;mi&gt;θ&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 14 */{&#xa;                    &quot;F_s=-kx&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;F_s=-kx\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;F&lt;/mi&gt;&lt;mi&gt;s&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mo&gt;−&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;k&lt;/mi&gt;&lt;mi&gt;x&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 15 */{&#xa;                    &quot;U_s=1/2kx^2&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;U_s=1/2kx^2\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;U&lt;/mi&gt;&lt;mi&gt;s&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mfrac&gt;&lt;mn&gt;1&lt;/mn&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/mfrac&gt;&lt;mi&gt;k&lt;/mi&gt;&lt;/mrow&gt;&lt;msup&gt;&lt;mi&gt;x&lt;/mi&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/msup&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 16 */{&#xa;                    &quot;T_s=2π√{m/k}&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;T_s=2π√{m/k}\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;T&lt;/mi&gt;&lt;mi&gt;s&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;mi&gt;π&lt;/mi&gt;&lt;/mrow&gt;&lt;msqrt&gt;&lt;mfrac&gt;&lt;mi&gt;m&lt;/mi&gt;&lt;mi&gt;k&lt;/mi&gt;&lt;/mfrac&gt;&lt;/msqrt&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 17 */{&#xa;                    &quot;T_p=2π√{l/g}&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;T_p=2π√{l/g}\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;T&lt;/mi&gt;&lt;mi&gt;p&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;mi&gt;π&lt;/mi&gt;&lt;/mrow&gt;&lt;msqrt&gt;&lt;mfrac&gt;&lt;mi&gt;l&lt;/mi&gt;&lt;mi&gt;g&lt;/mi&gt;&lt;/mfrac&gt;&lt;/msqrt&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 18 */{&#xa;                    &quot;T=1/f&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;T=1/f\&quot;&gt;&lt;mrow&gt;&lt;mi&gt;T&lt;/mi&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mfrac&gt;&lt;mn&gt;1&lt;/mn&gt;&lt;mi&gt;f&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 19 */{&#xa;                    &quot;F_G=-{Gm_1m_2}/{r^2}&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;F_G=-{Gm_1m_2}/{r^2}\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;F&lt;/mi&gt;&lt;mi&gt;G&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mo&gt;−&lt;/mo&gt;&lt;mfrac&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;G&lt;/mi&gt;&lt;msub&gt;&lt;mi&gt;m&lt;/mi&gt;&lt;mn&gt;1&lt;/mn&gt;&lt;/msub&gt;&lt;/mrow&gt;&lt;msub&gt;&lt;mi&gt;m&lt;/mi&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/msub&gt;&lt;/mrow&gt;&lt;msup&gt;&lt;mi&gt;r&lt;/mi&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/msup&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 20 */{&#xa;                    &quot;U_G=-{Gm_1m_2}/{r}&quot;,&#xa;                    &quot;&lt;math alttext=\&quot;U_G=-{Gm_1m_2}/{r}\&quot;&gt;&lt;mrow&gt;&lt;msub&gt;&lt;mi&gt;U&lt;/mi&gt;&lt;mi&gt;G&lt;/mi&gt;&lt;/msub&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mo&gt;−&lt;/mo&gt;&lt;mfrac&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;G&lt;/mi&gt;&lt;msub&gt;&lt;mi&gt;m&lt;/mi&gt;&lt;mn&gt;1&lt;/mn&gt;&lt;/msub&gt;&lt;/mrow&gt;&lt;msub&gt;&lt;mi&gt;m&lt;/mi&gt;&lt;mn&gt;2&lt;/mn&gt;&lt;/msub&gt;&lt;/mrow&gt;&lt;mi&gt;r&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;&#xa;    /**/};&#xa;private final String[][] VARIABLES = {&#xa;            // 0-variable 1-description&#xa;            /* 0 */{ &quot;a&quot;, &quot;Acceleration&quot; },&#xa;            /* 1 */{ &quot;F&quot;, &quot;Force&quot; },&#xa;            /* 2 */{ &quot;f&quot;, &quot;Frequency&quot; },&#xa;            /* 3 */{ &quot;h&quot;, &quot;Height&quot; },&#xa;            /* 4 */{ &quot;J&quot;, &quot;Impulse&quot; },&#xa;            /* 5 */{ &quot;K&quot;, &quot;Kinetic Energy&quot; },&#xa;            /* 6 */{ &quot;k&quot;, &quot;Spring Constant&quot; },&#xa;            /* 7 */{ &quot;l&quot;, &quot;Length&quot; },&#xa;            /* 8 */{ &quot;m&quot;, &quot;Mass&quot; },&#xa;            /* 9 */{ &quot;N&quot;, &quot;Normal Force&quot; },&#xa;            /* 10 */{ &quot;P&quot;, &quot;Power&quot; },&#xa;            /* 11 */{ &quot;p&quot;, &quot;Momentum&quot; },&#xa;            /* 12 */{ &quot;r&quot;, &quot;Radius&quot; },&#xa;            /* 13 */{ &quot;T&quot;, &quot;period&quot; },&#xa;            /* 14 */{ &quot;t&quot;, &quot;Time&quot; },&#xa;            /* 15 */{ &quot;U&quot;, &quot;Potential Energy&quot; },&#xa;            /* 16 */{ &quot;ν&quot;, &quot;Velocity&quot; },&#xa;            /* 17 */{ &quot;W&quot;, &quot;Work Done On System&quot; },&#xa;            /* 18 */{ &quot;x&quot;, &quot;Position&quot; },&#xa;            /* 19 */{ &quot;μ&quot;, &quot;Coefficient Of Friction&quot; },&#xa;            /* 20 */{ &quot;θ&quot;, &quot;Angle&quot; },&#xa;            /* 21 */{ &quot;τ&quot;, &quot;Torque&quot; }&#xa;    /**/};&#xa;/**&#xa;     * Unknown variables must be upper case. Lower case letters may be replaced by randomly generated numbers&#xa;     */&#xa;    private final String[][] ALGEBRA = {&#xa;            /* 0 */{ &quot;a+X+b=c+d*e&quot;, &quot;&lt;math alttext=\&quot;a+X+b=c+d*e\&quot;&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mi&gt;X&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mi&gt;b&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;c&lt;/mi&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;d&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;e&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 1 */{ &quot;a*b=c*X*d*e&quot;, &quot;&lt;math alttext=\&quot;a*b=c*X*d*e\&quot;&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;b&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;c&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;X&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;d&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;e&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 2 */{ &quot;X+a*b=c*d+e&quot;, &quot;&lt;math alttext=\&quot;X+a*b=c*d+e\&quot;&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;X&lt;/mi&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;b&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;c&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;d&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mi&gt;e&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 3 */{ &quot;a*X=d/e&quot;, &quot;&lt;math alttext=\&quot;a*X=d/e\&quot;&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;X&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mfrac&gt;&lt;mi&gt;d&lt;/mi&gt;&lt;mi&gt;e&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 4 */{ &quot;a+X=d/e&quot;, &quot;&lt;math alttext=\&quot;a+X=d/e\&quot;&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mi&gt;X&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mfrac&gt;&lt;mi&gt;d&lt;/mi&gt;&lt;mi&gt;e&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 5 */{ &quot;a+b*X=c*d/e&quot;, &quot;&lt;math alttext=\&quot;a+b*X=c*d/e\&quot;&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;b&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;X&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;c&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mfrac&gt;&lt;mi&gt;d&lt;/mi&gt;&lt;mi&gt;e&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 6 */{ &quot;a+X/b=c/d&quot;, &quot;&lt;math alttext=\&quot;a+X/b=c/d\&quot;&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mfrac&gt;&lt;mi&gt;X&lt;/mi&gt;&lt;mi&gt;b&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mfrac&gt;&lt;mi&gt;c&lt;/mi&gt;&lt;mi&gt;d&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 7 */{ &quot;a/X=c*d&quot;, &quot;&lt;math alttext=\&quot;a/X=c*d\&quot;&gt;&lt;mrow&gt;&lt;mfrac&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mi&gt;X&lt;/mi&gt;&lt;/mfrac&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt;c&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;d&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;            /* 8 */{ &quot;a*X*b+c/d=e*f*g/h&quot;, &quot;&lt;math alttext=\&quot;a*X*b+c/d=e*f*g/h\&quot;&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;a&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;X&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;b&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;+&lt;/mo&gt;&lt;mfrac&gt;&lt;mi&gt;c&lt;/mi&gt;&lt;mi&gt;d&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;mo&gt;=&lt;/mo&gt;&lt;mrow&gt;&lt;mrow&gt;&lt;mi&gt;e&lt;/mi&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mi&gt;f&lt;/mi&gt;&lt;/mrow&gt;&lt;mo&gt;*&lt;/mo&gt;&lt;mfrac&gt;&lt;mi&gt;g&lt;/mi&gt;&lt;mi&gt;h&lt;/mi&gt;&lt;/mfrac&gt;&lt;/mrow&gt;&lt;/mrow&gt;&lt;/math&gt;&quot; },&#xa;&#xa;    /**/};&#xa;private final String[][] QUANTITIES = {&#xa;            // 0-quantity 1-description&#xa;            /* 0 */{ &quot;a&quot;, &quot;Acceleration&quot; },&#xa;            /* 1 */{ &quot;F&quot;, &quot;Force&quot; },&#xa;            /* 2 */{ &quot;f&quot;, &quot;Frequency&quot; },&#xa;            /* 3 */// { &quot;h&quot;, &quot;Height&quot; },&#xa;            /* 4 */{ &quot;J&quot;, &quot;Impulse&quot; },&#xa;            /* 5 */{ &quot;K&quot;, &quot;Energy&quot; },&#xa;            /* 6 */{ &quot;k&quot;, &quot;Spring Constant&quot; },&#xa;            /* 7 */{ &quot;l&quot;, &quot;Length&quot; },&#xa;            /* 8 */{ &quot;m&quot;, &quot;Mass&quot; },&#xa;            /* 9 */{ &quot;N&quot;, &quot;Normal Force&quot; },&#xa;            /* 10 */{ &quot;P&quot;, &quot;Power&quot; },&#xa;            /* 11 */{ &quot;p&quot;, &quot;Momentum&quot; },&#xa;            /* 12 */{ &quot;r&quot;, &quot;Radius&quot; },&#xa;            /* 13 */{ &quot;T&quot;, &quot;period&quot; },&#xa;            /* 14 */{ &quot;t&quot;, &quot;Time&quot; },&#xa;            /* 15 */{ &quot;U&quot;, &quot;Potential Energy&quot; },&#xa;            /* 16 */{ &quot;ν&quot;, &quot;Velocity&quot; },&#xa;            /* 17 */{ &quot;W&quot;, &quot;Work Done On System&quot; },&#xa;            /* 18 */{ &quot;x&quot;, &quot;Position&quot; },&#xa;            /* 19 */{ &quot;μ&quot;, &quot;Coefficient Of Friction&quot; },&#xa;            /* 20 */{ &quot;θ&quot;, &quot;Angle&quot; },&#xa;            /* 21 */{ &quot;τ&quot;, &quot;Torque&quot; }&#xa;    /**/};&#xa;private final String[][] UNITS = {&#xa;    /* 0 */{ &quot;0&quot;, &quot;?&quot; },&#xa;    /* 1 */{ &quot;1&quot;, &quot;?&quot; },&#xa;    /* 2 */{ &quot;2&quot;, &quot;?&quot; }&#xa;    /**/};&#xa;public final String[] functions = { &quot;+&quot;, &quot;-&quot;, &quot;x&quot;, &quot;÷&quot; };&#xa;// TODO mabe by enum just to see the variables, make into numbers for&#xa;    // deployment&#xa;    // association between {(0)equations, (1)variables}&#xa;    private final short[][] ASS_EQ_VAR = {&#xa;&#xa;    /* 0 ν=ν_0 + at */{ 0, V.ν.i }, { 0, V.a.i }, { 0, V.t.i },&#xa;    /* 1 x=x_0 + ν_0t + 1/2at^2 */{ 1, V.x.i }, { 1, V.a.i }, { 1, V.ν.i },&#xa;            { 1, V.t.i },&#xa;            /* 2 ν^2=ν_0^2 + 2a(x - x_0) */{ 2, V.ν.i }, { 2, V.a.i },&#xa;            { 2, V.x.i },&#xa;            /* 3 ∑F=F_{net} = ma */{ 3, V.F.i }, { 3, V.m.i }, { 3, V.a.i },&#xa;            /* 4 F_{fric}≤μN */{ 4, V.F.i }, { 4, V.μ.i }, { 4, V.N.i },&#xa;            /* 5 a_c={ν^2}/r */{ 5, V.a.i }, { 5, V.ν.i }, { 5, V.r.i },&#xa;            /* 6 τ=rF sinθ */{ 6, V.τ.i }, { 6, V.r.i }, { 6, V.F.i },&#xa;            { 6, V.θ.i },&#xa;            /* 7 p=mν */{ 7, V.p.i }, { 7, V.m.i }, { 7, V.ν.i },&#xa;            /* 8 J=F\\Δt = \\Δp */{ 8, V.J.i }, { 8, V.F.i }, { 8, V.t.i },&#xa;            { 8, V.p.i },&#xa;            /* 9 K=1/2mν^2 */{ 9, V.K.i }, { 9, V.m.i }, { 9, V.ν.i },&#xa;            /* 10 \\U_g=mgh */{ 10, V.U.i }, { 10, V.m.i }, { 10, V.h.i },&#xa;            /* 11 W=F\\Δrcosθ */{ 11, V.W.i }, { 11, V.F.i }, { 11, V.r.i },&#xa;            { 11, V.θ.i },&#xa;            /* 12 P_{avg}=W/Δt */{ 12, V.P.i }, { 12, V.W.i }, { 12, V.t.i },&#xa;            /* 13 P=Fνcosθ */{ 13, V.P.i }, { 13, V.F.i }, { 13, V.ν.i },&#xa;            { 13, V.θ.i },&#xa;            /* 14 F_s=-kx */{ 14, V.F.i }, { 14, V.k.i }, { 14, V.x.i },&#xa;            /* 15 U_s=1/2kx^2 */{ 15, V.U.i }, { 15, V.k.i }, { 15, V.x.i },&#xa;            /* 16 T_s=2π√{m/k} */{ 16, V.T.i }, { 16, V.m.i }, { 16, V.k.i },&#xa;            /* 17 T_p=2π√{l/g} */{ 17, V.T.i }, { 17, V.l.i },&#xa;            /* 18 T=1/f */{ 18, V.T.i }, { 18, V.f.i },&#xa;            /* 19 F_G=-{Gm_1m_2}/{r^2} */{ 19, V.F.i }, { 19, V.m.i },&#xa;            { 19, V.r.i },&#xa;            /* 20 U_G=-{Gm_1m_2}/{r} */{ 20, V.U.i }, { 20, V.m.i },&#xa;            { 20, V.r.i },&#xa;    /**/};&#xa;// association of (0)variables (1)quantities&#xa;    private final short[][] ASS_QUAN_VAR = {&#xa;    /**/{ 0, 0 },&#xa;    /**/{ 0, 0 },&#xa;    /**/{ 0, 0 } };&#xa;// association of (0)quantities (1)units&#xa;    private final short[][] ASS_QUAN_UNIT = {&#xa;    /**/{ 0, 0 },&#xa;    /**/{ 0, 0 },&#xa;    /**/{ 0, 0 } };" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="String[] getAll(byte[] attributeFlag)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getAll([B)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.createAlgBrowser()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="createAlgBrowser" TARGET_LABEL="getAll" MIDDLE_LABEL="getAll"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.fillVarList()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="fillVarList" TARGET_LABEL="getAll" MIDDLE_LABEL="getAll"/>
</node>

<node TEXT="String getAttribute(byte[] attributeFlag, String element)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getAttribute([BQString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.fillSummary(Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="fillSummary" TARGET_LABEL="getAttribute" MIDDLE_LABEL="getAttribute"/>
</node>

<node TEXT="String getAlgAttribute(byte[] attribute, String el)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getAlgAttribute([BQString;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="String[] getEquationsByVariable(String variable)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getEquationsByVariable(QString;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="String[] getVariablesByEquation(String equation)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getVariablesByEquation(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.fillSummary(Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="fillSummary" TARGET_LABEL="getVariablesByEquation" MIDDLE_LABEL="getVariablesByEquation"/>
</node>

<node TEXT="String[] getVariablesByQuantity(String quantity)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getVariablesByQuantity(QString;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="String[] getUnitsByQuantity(String quantity)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getUnitsByQuantity(QString;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="String[] getEquationsByVariables(Set&lt;String&gt; varStringsSet)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getEquationsByVariables(QSet&lt;QString;&gt;;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.onVarSelect(Ljava/util/Set&lt;Ljava/lang/String;&gt;;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onVarSelect" TARGET_LABEL="getEquationsByVariables" MIDDLE_LABEL="getEquationsByVariables"/>
</node>

<node TEXT="short getElementIndex(byte typeOfElementFlag, String element)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getElementIndex(BQString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getUnitsByQuantity(Ljava/lang/String;)[Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getUnitsByQuantity" TARGET_LABEL="getElementIndex" MIDDLE_LABEL="getElementIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getEquationsByVariables(Ljava/util/Set&lt;Ljava/lang/String;&gt;;)[Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getEquationsByVariables" TARGET_LABEL="getElementIndex" MIDDLE_LABEL="getElementIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getEquationsByVariable(Ljava/lang/String;)[Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getEquationsByVariable" TARGET_LABEL="getElementIndex" MIDDLE_LABEL="getElementIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getAttribute([BLjava/lang/String;)Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getAttribute" TARGET_LABEL="getElementIndex" MIDDLE_LABEL="getElementIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getVariablesByEquation(Ljava/lang/String;)[Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getVariablesByEquation" TARGET_LABEL="getElementIndex" MIDDLE_LABEL="getElementIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getVariablesByQuantity(Ljava/lang/String;)[Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getVariablesByQuantity" TARGET_LABEL="getElementIndex" MIDDLE_LABEL="getElementIndex"/>
</node>

<node TEXT="private static final long serialVersionUID = 1137715992680750870L;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void ElementNotFoundExeption()" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase~ElementNotFoundExeption;.ElementNotFoundExeption()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getAlgAttribute([BLjava/lang/String;)Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getAlgAttribute" TARGET_LABEL="ElementNotFoundExeption" MIDDLE_LABEL="ElementNotFoundExeption"/>
</node>

<node TEXT="void ElementNotFoundExeption(String s)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase~ElementNotFoundExeption;.ElementNotFoundExeption(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getUnitsByQuantity(Ljava/lang/String;)[Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getUnitsByQuantity" TARGET_LABEL="ElementNotFoundExeption" MIDDLE_LABEL="ElementNotFoundExeption"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getElementIndex(BLjava/lang/String;)S|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getElementIndex" TARGET_LABEL="ElementNotFoundExeption" MIDDLE_LABEL="ElementNotFoundExeption"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getEquationsByVariable(Ljava/lang/String;)[Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getEquationsByVariable" TARGET_LABEL="ElementNotFoundExeption" MIDDLE_LABEL="ElementNotFoundExeption"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getAttribute([BLjava/lang/String;)Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getAttribute" TARGET_LABEL="ElementNotFoundExeption" MIDDLE_LABEL="ElementNotFoundExeption"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getVariablesByEquation(Ljava/lang/String;)[Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getVariablesByEquation" TARGET_LABEL="ElementNotFoundExeption" MIDDLE_LABEL="ElementNotFoundExeption"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase;.getVariablesByQuantity(Ljava/lang/String;)[Ljava/lang/String;|Lcom/sciencegadgets/client/equationbrowser/ElementNotFoundExeption;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getVariablesByQuantity" TARGET_LABEL="ElementNotFoundExeption" MIDDLE_LABEL="ElementNotFoundExeption"/>
</node>

<node TEXT="// 0-variable 1-description&#xa;        /* 0 */a(&quot;Acceleration&quot;)&#xa;/* 1 */F(&quot;Force&quot;)&#xa;/* 2 */f(&quot;Frequency&quot;)&#xa;/* 3 */h(&quot;Height&quot;)&#xa;/* 4 */J(&quot;Impulse&quot;)&#xa;/* 5 */K(&quot;Kinetic Energy&quot;)&#xa;/* 6 */k(&quot;Spring Constant&quot;)&#xa;/* 7 */l(&quot;Length&quot;)&#xa;/* 8 */m(&quot;Mass&quot;)&#xa;/* 9 */N(&quot;Normal Force&quot;)&#xa;/* 10 */P(&quot;Power&quot;)&#xa;/* 11 */p(&quot;Momentum&quot;)&#xa;/* 12 */r(&quot;Radius&quot;)&#xa;/* 13 */T(&quot;period&quot;)&#xa;/* 14 */t(&quot;Time&quot;)&#xa;/* 15 */U(&quot;Potential Energy&quot;)&#xa;/* 16 */ν(&quot;Velocity&quot;)&#xa;/* 17 */W(&quot;Work on System&quot;)&#xa;/* 18 */x(&quot;Position&quot;)&#xa;/* 19 */μ(&quot;Coef. of Friction&quot;)&#xa;/* 20 */θ(&quot;Angle&quot;)&#xa;/* 21 */τ(&quot;Torque&quot;)&#xa;private final short i;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void V(String name)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationDatabase$V;.V(QString;)V" BACKGROUND_COLOR="#ffa07a">
</node>
</node>

<node TEXT="EquationBrowserEntry.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="EquationDatabase data = new EquationDatabase();&#xa;HorizontalPanel browserPanel = new HorizontalPanel();&#xa;private Grid eqGrid = new Grid(1, 1);&#xa;private Grid varGrid = new Grid(1, 1);&#xa;private Grid sumGrid = new Grid(1, 4);&#xa;private Grid algGrid = new Grid(1, 1);&#xa;private CheckBox multiSwitch = new CheckBox(&quot;Multi-Select&quot;);&#xa;private Set&lt;String&gt; selectedVars = new HashSet&lt;String&gt;();&#xa;private RadioButton modeSelectAlg = new RadioButton(&quot;mode&quot;, &quot;Algebra&quot;);&#xa;private RadioButton modeSelectSci = new RadioButton(&quot;mode&quot;, &quot;Science&quot;);&#xa;private Button sumButton = new Button(&quot;Use&quot;);&#xa;private Button combineEqButton = new Button(&quot;Combine&quot;);&#xa;private HashMap&lt;TextBox, String&gt; inputBinding = new HashMap&lt;TextBox, String&gt;();&#xa;public static HTML labelSumEq = new HTML(&quot;&quot;);" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void onModuleLoad()" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.onModuleLoad()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/AlgOutEntry$BackButtonHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="onModuleLoad" MIDDLE_LABEL="onModuleLoad"/>
</node>

<node TEXT="void createSciBrowser()" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.createSciBrowser()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$ModeSelectHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="createSciBrowser" MIDDLE_LABEL="createSciBrowser"/>
</node>

<node TEXT="void createAlgBrowser()" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.createAlgBrowser()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.onModuleLoad()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onModuleLoad" TARGET_LABEL="createAlgBrowser" MIDDLE_LABEL="createAlgBrowser"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$ModeSelectHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="createAlgBrowser" MIDDLE_LABEL="createAlgBrowser"/>
</node>

<node TEXT="void fillVarList()" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.fillVarList()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.createSciBrowser()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="createSciBrowser" TARGET_LABEL="fillVarList" MIDDLE_LABEL="fillVarList"/>
</node>

<node TEXT="void onVarSelect(Set&lt;String&gt; varSet)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.onVarSelect(QSet&lt;QString;&gt;;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$VarClickHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="onVarSelect" MIDDLE_LABEL="onVarSelect"/>
</node>

<node TEXT="void onEqSelect(String equation)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.onEqSelect(QString;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void fillSummary(String equation)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.fillSummary(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$EqClickHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="fillSummary" MIDDLE_LABEL="fillSummary"/>
</node>

<node TEXT="void parseJQMath(Element element)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.parseJQMath(QElement;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.onVarSelect(Ljava/util/Set&lt;Ljava/lang/String;&gt;;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onVarSelect" TARGET_LABEL="parseJQMath" MIDDLE_LABEL="parseJQMath"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.toMathML()Lcom/google/gwt/user/client/ui/HTML;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toMathML" TARGET_LABEL="parseJQMath" MIDDLE_LABEL="parseJQMath"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.createSciBrowser()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="createSciBrowser" TARGET_LABEL="parseJQMath" MIDDLE_LABEL="parseJQMath"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.fillSummary(Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="fillSummary" TARGET_LABEL="parseJQMath" MIDDLE_LABEL="parseJQMath"/>
</node>

<node TEXT="void sendAlgebraEquation(String equation)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.sendAlgebraEquation(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$EqClickHandler;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="sendAlgebraEquation" MIDDLE_LABEL="sendAlgebraEquation"/>
</node>

<node TEXT="HTMLTable table;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void EqClickHandler(HTMLTable table)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$EqClickHandler;.EqClickHandler(QHTMLTable;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.createSciBrowser()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="createSciBrowser" TARGET_LABEL="EqClickHandler" MIDDLE_LABEL="EqClickHandler"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.createAlgBrowser()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="createAlgBrowser" TARGET_LABEL="EqClickHandler" MIDDLE_LABEL="EqClickHandler"/>
</node>

<node TEXT="void onClick(ClickEvent event)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$EqClickHandler;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>

<node TEXT="HTMLTable table;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void VarClickHandler(HTMLTable table)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$VarClickHandler;.VarClickHandler(QHTMLTable;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.createSciBrowser()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="createSciBrowser" TARGET_LABEL="VarClickHandler" MIDDLE_LABEL="VarClickHandler"/>
</node>

<node TEXT="void onClick(ClickEvent event)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$VarClickHandler;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>

<node TEXT="void onClick(ClickEvent event)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$MultiSwitchClickHandler;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>

<node TEXT="String mode = &quot;algebra&quot;;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void ModeSelectHandler(String mode)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$ModeSelectHandler;.ModeSelectHandler(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.onModuleLoad()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onModuleLoad" TARGET_LABEL="ModeSelectHandler" MIDDLE_LABEL="ModeSelectHandler"/>
</node>

<node TEXT="void onClick(ClickEvent event)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$ModeSelectHandler;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>

<node TEXT="TextBox textBox;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void FindClickHandler(TextBox textBox)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$FindClickHandler;.FindClickHandler(QTextBox;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry;.fillSummary(Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="fillSummary" TARGET_LABEL="FindClickHandler" MIDDLE_LABEL="FindClickHandler"/>
</node>

<node TEXT="void onClick(ClickEvent arg0)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$FindClickHandler;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>

<node TEXT="void onClick(ClickEvent arg0)" ID="Lcom/sciencegadgets/client/equationbrowser/EquationBrowserEntry$UseEquation;.onClick(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/event/dom/client/ClickEvent;.dispatch(Lcom/google/gwt/event/dom/client/ClickHandler;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dispatch" TARGET_LABEL="onClick" MIDDLE_LABEL="onClick"/>
</node>
</node>
</node>

<node TEXT="com.sciencegadgets.client.equationtree" BACKGROUND_COLOR="#eee8aa">

<node TEXT="MathMLParser.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="public Node elLeft;&#xa;public Node elEquals;&#xa;public Node elRight;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void MathMLParser(HTML mathMLequation)" ID="Lcom/sciencegadgets/client/equationtree/MathMLParser;.MathMLParser(QHTML;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;Lcom/google/gwt/user/client/ui/HTML;)V|Lcom/sciencegadgets/client/TopNodesNotFoundException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MLtoMLTree" TARGET_LABEL="MathMLParser" MIDDLE_LABEL="MathMLParser"/>
</node>

<node TEXT="void addChildren(Node mathMLNode, Boolean isLeft)" ID="Lcom/sciencegadgets/client/equationtree/MathMLParser;.addChildren(QNode;QBoolean;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLParser;.(Lcom/google/gwt/user/client/ui/HTML;)V|Lcom/sciencegadgets/client/TopNodesNotFoundException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathMLParser" TARGET_LABEL="addChildren" MIDDLE_LABEL="addChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLParser;.addChildren(Lcom/google/gwt/dom/client/Node;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChildren" TARGET_LABEL="addChildren" MIDDLE_LABEL="addChildren"/>
</node>

<node TEXT="void onRootsFound(Node nodeLeft, Node nodeEquals, Node nodeRight)" ID="Lcom/sciencegadgets/client/equationtree/MathMLParser;.onRootsFound(QNode;QNode;QNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLParser;.(Lcom/google/gwt/user/client/ui/HTML;)V|Lcom/sciencegadgets/client/TopNodesNotFoundException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathMLParser" TARGET_LABEL="onRootsFound" MIDDLE_LABEL="onRootsFound"/>
</node>

<node TEXT="void onVisitNode(Node currentNode, Boolean isLeft, int indexOfSiblings)" ID="Lcom/sciencegadgets/client/equationtree/MathMLParser;.onVisitNode(QNode;QBoolean;I)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLParser;.addChildren(Lcom/google/gwt/dom/client/Node;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChildren" TARGET_LABEL="onVisitNode" MIDDLE_LABEL="onVisitNode"/>
</node>

<node TEXT="void onGoingToNextChild(Node currentNode)" ID="Lcom/sciencegadgets/client/equationtree/MathMLParser;.onGoingToNextChild(QNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLParser;.addChildren(Lcom/google/gwt/dom/client/Node;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChildren" TARGET_LABEL="onGoingToNextChild" MIDDLE_LABEL="onGoingToNextChild"/>
</node>
</node>

<node TEXT="MathMLBindingTree.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private MathMLBindingTree tree = this;&#xa;private MathMLBindingNode root;&#xa;private MathMLBindingNode leftSide;&#xa;private MathMLBindingNode equals;&#xa;private MathMLBindingNode rightSide;&#xa;private LinkedList&lt;MLElementWrapper&gt; wrappers = new LinkedList&lt;MLElementWrapper&gt;();&#xa;private HTML mathML;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void MathMLBindingTree(HTML mathML, Boolean isParsedForMath)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.MathMLBindingTree(QHTML;QBoolean;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(Lcom/google/gwt/user/client/ui/HTML;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="transport" TARGET_LABEL="MathMLBindingTree" MIDDLE_LABEL="MathMLBindingTree"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.selectEquation(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="selectEquation" TARGET_LABEL="MathMLBindingTree" MIDDLE_LABEL="MathMLBindingTree"/>
</node>

<node TEXT="HTML toMathML()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.toMathML()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(Lcom/google/gwt/user/client/ui/HTML;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="transport" TARGET_LABEL="toMathML" MIDDLE_LABEL="toMathML"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="toMathML" MIDDLE_LABEL="toMathML"/>
</node>

<node TEXT="HTML getMathML()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.getMathML()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.selectEquation(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="selectEquation" TARGET_LABEL="getMathML" MIDDLE_LABEL="getMathML"/>
</node>

<node TEXT="MathMLBindingNode getRoot()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.getRoot()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathTreeToML" TARGET_LABEL="getRoot" MIDDLE_LABEL="getRoot"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.change(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="change" TARGET_LABEL="getRoot" MIDDLE_LABEL="getRoot"/>
</node>

<node TEXT="MathMLBindingNode getLeftSide()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.getLeftSide()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getLeftSide" MIDDLE_LABEL="getLeftSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.addFirstLayer(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)[I" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addFirstLayer" TARGET_LABEL="getLeftSide" MIDDLE_LABEL="getLeftSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getLeftSide" MIDDLE_LABEL="getLeftSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.draw(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="draw" TARGET_LABEL="getLeftSide" MIDDLE_LABEL="getLeftSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getLeftSide" MIDDLE_LABEL="getLeftSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="getLeftSide" MIDDLE_LABEL="getLeftSide"/>
</node>

<node TEXT="MathMLBindingNode getRightSide()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.getRightSide()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getRightSide" MIDDLE_LABEL="getRightSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.addFirstLayer(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)[I" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addFirstLayer" TARGET_LABEL="getRightSide" MIDDLE_LABEL="getRightSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getRightSide" MIDDLE_LABEL="getRightSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.draw(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="draw" TARGET_LABEL="getRightSide" MIDDLE_LABEL="getRightSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getRightSide" MIDDLE_LABEL="getRightSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="getRightSide" MIDDLE_LABEL="getRightSide"/>
</node>

<node TEXT="void setLeftSide(MathMLBindingNode jNode)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.setLeftSide(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setLeftSide" MIDDLE_LABEL="setLeftSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setLeftSide" MIDDLE_LABEL="setLeftSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setLeftSide" MIDDLE_LABEL="setLeftSide"/>
</node>

<node TEXT="void setRightSide(MathMLBindingNode jNode)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.setRightSide(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setRightSide" MIDDLE_LABEL="setRightSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setRightSide" MIDDLE_LABEL="setRightSide"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setRightSide" MIDDLE_LABEL="setRightSide"/>
</node>

<node TEXT="MathMLBindingNode getEquals()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.getEquals()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="LinkedList&lt;MLElementWrapper&gt; getWrappers()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.getWrappers()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.selectEquation(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="selectEquation" TARGET_LABEL="getWrappers" MIDDLE_LABEL="getWrappers"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="getWrappers" MIDDLE_LABEL="getWrappers"/>
</node>

<node TEXT="LinkedList&lt;MLElementWrapper&gt; wrapTree()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.wrapTree()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/Boolean;)V|Lcom/sciencegadgets/client/TopNodesNotFoundException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathMLBindingTree" TARGET_LABEL="wrapTree" MIDDLE_LABEL="wrapTree"/>
</node>

<node TEXT="void wrapChildren(MathMLBindingNode jNode)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.wrapChildren(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.wrapTree()Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="wrapTree" TARGET_LABEL="wrapChildren" MIDDLE_LABEL="wrapChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.wrapChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="wrapChildren" TARGET_LABEL="wrapChildren" MIDDLE_LABEL="wrapChildren"/>
</node>

<node TEXT="private Node domNode;&#xa;private Type type;&#xa;private String symbol;&#xa;private String tag;&#xa;private MLElementWrapper wrapper;&#xa;private MathMLBindingNode parent;&#xa;private List&lt;MathMLBindingNode&gt; children = new LinkedList&lt;MathMLBindingNode&gt;();&#xa;private Boolean isHidden = false;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void MathMLBindingNode(Node domNode, String tag, Type type, String symbol)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.MathMLBindingNode(QNode;QString;QType;QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.change(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="change" TARGET_LABEL="MathMLBindingNode" MIDDLE_LABEL="MathMLBindingNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="MathMLBindingNode" MIDDLE_LABEL="MathMLBindingNode"/>
</node>

<node TEXT="void MathMLBindingNode(Node node)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.MathMLBindingNode(QNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.onRootsFound(Lcom/google/gwt/dom/client/Node;Lcom/google/gwt/dom/client/Node;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onRootsFound" TARGET_LABEL="MathMLBindingNode" MIDDLE_LABEL="MathMLBindingNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.onVisitNode(Lcom/google/gwt/dom/client/Node;Ljava/lang/Boolean;I)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onVisitNode" TARGET_LABEL="MathMLBindingNode" MIDDLE_LABEL="MathMLBindingNode"/>
</node>

<node TEXT="void MathMLBindingNode(String tag, Type type, String symbol)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.MathMLBindingNode(QString;QType;QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.add(ILjava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;Ljava/lang/String;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="add" TARGET_LABEL="MathMLBindingNode" MIDDLE_LABEL="MathMLBindingNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.encase(Ljava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="encase" TARGET_LABEL="MathMLBindingNode" MIDDLE_LABEL="MathMLBindingNode"/>
</node>

<node TEXT="MathMLBindingNode encase(String tag, Type type)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.encase(QString;QType;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="encase" MIDDLE_LABEL="encase"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="encase" MIDDLE_LABEL="encase"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="encase" MIDDLE_LABEL="encase"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="encase" MIDDLE_LABEL="encase"/>
</node>

<node TEXT="MathMLBindingNode add(int index, String tag, Type type, String symbol)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.add(IQString;QType;QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.add(Ljava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;Ljava/lang/String;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="add" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
</node>

<node TEXT="void add(int index, MathMLBindingNode mathMLBindingNode)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.add(IQMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNestedMrows()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNestedMrows" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.add(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="add" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.add(ILjava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;Ljava/lang/String;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="add" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.encase(Ljava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="encase" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
</node>

<node TEXT="void add(MathMLBindingNode mathMLBindingNode)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.add(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.change(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="change" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.encase(Ljava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="encase" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.onVisitNode(Lcom/google/gwt/dom/client/Node;Ljava/lang/Boolean;I)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onVisitNode" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
</node>

<node TEXT="MathMLBindingNode add(String tag, Type type, String symbol)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.add(QString;QType;QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="add" MIDDLE_LABEL="add"/>
</node>

<node TEXT="List&lt;MathMLBindingNode&gt; getChildren()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getChildren()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNestedMrows()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNestedMrows" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.getNextLayerCounts(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;BLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getNextLayerCounts" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.wrapChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="wrapChildren" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.parseTree(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="parseTree" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.remove()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="remove" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getIndex()I" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getIndex" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getSibling(I)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getSibling" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.addChild(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChild" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
</node>

<node TEXT="MathMLBindingNode getFirstChild()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getFirstChild()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="getFirstChild" MIDDLE_LABEL="getFirstChild"/>
</node>

<node TEXT="MathMLBindingNode getChildAt(int index)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getChildAt(I)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getChildAt" MIDDLE_LABEL="getChildAt"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getChildAt" MIDDLE_LABEL="getChildAt"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getSibling(I)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getSibling" TARGET_LABEL="getChildAt" MIDDLE_LABEL="getChildAt"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.negatePropagate(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="negatePropagate" TARGET_LABEL="getChildAt" MIDDLE_LABEL="getChildAt"/>
</node>

<node TEXT="int getChildCount()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getChildCount()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.getNextLayerCounts(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;BLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getNextLayerCounts" TARGET_LABEL="getChildCount" MIDDLE_LABEL="getChildCount"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.wrapChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="wrapChildren" TARGET_LABEL="getChildCount" MIDDLE_LABEL="getChildCount"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.parseTree(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="parseTree" TARGET_LABEL="getChildCount" MIDDLE_LABEL="getChildCount"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="getChildCount" MIDDLE_LABEL="getChildCount"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.draw(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="draw" TARGET_LABEL="getChildCount" MIDDLE_LABEL="getChildCount"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="getChildCount" MIDDLE_LABEL="getChildCount"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.negatePropagate(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="negatePropagate" TARGET_LABEL="getChildCount" MIDDLE_LABEL="getChildCount"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.addChild(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChild" TARGET_LABEL="getChildCount" MIDDLE_LABEL="getChildCount"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="getChildCount" MIDDLE_LABEL="getChildCount"/>
</node>

<node TEXT="MathMLBindingNode getNextSibling()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getNextSibling()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getNextSibling" MIDDLE_LABEL="getNextSibling"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getNextSibling" MIDDLE_LABEL="getNextSibling"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getNextSibling" MIDDLE_LABEL="getNextSibling"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getNextSibling" MIDDLE_LABEL="getNextSibling"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="getNextSibling" MIDDLE_LABEL="getNextSibling"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="getNextSibling" MIDDLE_LABEL="getNextSibling"/>
</node>

<node TEXT="MathMLBindingNode getPrevSibling()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getPrevSibling()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getPrevSibling" MIDDLE_LABEL="getPrevSibling"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getPrevSibling" MIDDLE_LABEL="getPrevSibling"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getPrevSibling" MIDDLE_LABEL="getPrevSibling"/>
</node>

<node TEXT="MathMLBindingNode getSibling(int indexesAway)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getSibling(I)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getPrevSibling()Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getPrevSibling" TARGET_LABEL="getSibling" MIDDLE_LABEL="getSibling"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getNextSibling()Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getNextSibling" TARGET_LABEL="getSibling" MIDDLE_LABEL="getSibling"/>
</node>

<node TEXT="void remove()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.remove()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNestedMrows()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNestedMrows" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.encase(Ljava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="encase" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="remove" MIDDLE_LABEL="remove"/>
</node>

<node TEXT="int getIndex()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getIndex()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNestedMrows()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNestedMrows" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.findChange(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="findChange" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.encase(Ljava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="encase" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.checkIsHidden()Ljava/lang/Boolean;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="checkIsHidden" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="getIndex" MIDDLE_LABEL="getIndex"/>
</node>

<node TEXT="MathMLBindingNode getParent()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getParent()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNestedMrows()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNestedMrows" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getSibling(I)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getSibling" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.encase(Ljava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="encase" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.onVisitNode(Lcom/google/gwt/dom/client/Node;Ljava/lang/Boolean;I)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onVisitNode" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.checkIsHidden()Ljava/lang/Boolean;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="checkIsHidden" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="getParent" MIDDLE_LABEL="getParent"/>
</node>

<node TEXT="Node getDomNode()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getDomNode()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;.(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Ljava/lang/Boolean;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MLElementWrapper" TARGET_LABEL="getDomNode" MIDDLE_LABEL="getDomNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="getDomNode" MIDDLE_LABEL="getDomNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.encase(Ljava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="encase" TARGET_LABEL="getDomNode" MIDDLE_LABEL="getDomNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.addChild(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChild" TARGET_LABEL="getDomNode" MIDDLE_LABEL="getDomNode"/>
</node>

<node TEXT="void setDomNode(Node node)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.setDomNode(QNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.addChild(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChild" TARGET_LABEL="setDomNode" MIDDLE_LABEL="setDomNode"/>
</node>

<node TEXT="String toString()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.toString()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getParent()Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getParent" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/storage/client/StorageMap;.get(Ljava/lang/Object;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="get" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/MethodDispatch;.invoke(Lcom/google/gwt/dev/shell/JsValue;[Lcom/google/gwt/dev/shell/JsValue;Lcom/google/gwt/dev/shell/JsValue;)Z" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="invoke" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/log/SwingLoggerPanel;.treeLogTraverse(Ljava/lang/StringBuilder;Ljavax/swing/tree/TreeNode;I)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="treeLogTraverse" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.findChange(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="findChange" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/javac/BytecodeSignatureMaker$CompileDependencyVisitor;.visitField(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)Lcom/google/gwt/dev/asm/FieldVisitor;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="visitField" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.findChange(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="findChange" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getSibling(I)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getSibling" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/validation/client/BaseMessageInterpolator$2154;.apply(Ljava/lang/String;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="apply" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/rpc/server/CommandSerializationUtil$9788;.set(Ljava/lang/Object;JLjava/lang/Object;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="set" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/js/rhino/JavaScriptException;.(Ljava/lang/Object;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="JavaScriptException" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/log/SwingLoggerPanel;.valueChanged(Ljavax/swing/event/TreeSelectionEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="valueChanged" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.findChange(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="findChange" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/BrowserChannelServer;.invokeJavascript(Lcom/google/gwt/dev/shell/CompilingClassLoader;Lcom/google/gwt/dev/shell/JsValueOOPHM;Ljava/lang/String;[Lcom/google/gwt/dev/shell/JsValueOOPHM;Lcom/google/gwt/dev/shell/JsValueOOPHM;)V|Ljava/lang/Throwable;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="invokeJavascript" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.findChange(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="findChange" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/util/JsniRef;.equals(Ljava/lang/Object;)Z" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="equals" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/asm/util/AbstractVisitor;.printList(Ljava/io/PrintWriter;Ljava/util/List;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="printList" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.encase(Ljava/lang/String;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$Type;)Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="encase" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.isFunction()Ljava/lang/Boolean;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="isFunction" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/storage/client/StorageMap;.containsKey(Ljava/lang/Object;)Z" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="containsKey" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.checkIsHidden()Ljava/lang/Boolean;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="checkIsHidden" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/tomcat/CommonsLoggerAdapter;.doLog(Lcom/google/gwt/core/ext/TreeLogger$Type;Ljava/lang/Object;Ljava/lang/Throwable;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="doLog" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/WrapDragController;.dragStart()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="dragStart" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/server/rpc/RPCRequest;.toString()Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toString" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.findChange(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="findChange" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/web/bindery/requestfactory/server/Resolver$ResolutionKey;.toString()Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toString" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/storage/client/StorageMap;.remove(Ljava/lang/Object;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="remove" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/storage/client/StorageMap$StorageEntrySet;.remove(Ljava/lang/Object;)Z" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="remove" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/rebind/AbstractMethodCreator;.println(Ljava/lang/Object;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="println" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/jjs/impl/GenerateJavaAST$JavaASTGenerationVisitor;.translateException(Ljava/lang/Object;Ljava/lang/Throwable;)Lcom/google/gwt/dev/jjs/InternalCompilerException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="translateException" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/benchmarks/BenchmarkReport$BenchmarkXml;.toElement(Lorg/w3c/dom/Document;Lcom/google/gwt/benchmarks/client/impl/Trial;)Lorg/w3c/dom/Element;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toElement" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/js/rhino/Node;.toString()Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toString" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/util/msg/FormatterToString;.format(Ljava/lang/Object;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="format" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/web/bindery/autobean/vm/impl/JsonSplittable;.makeSplittable(Ljava/lang/Object;)Lcom/google/web/bindery/autobean/vm/impl/JsonSplittable;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="makeSplittable" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.addChild(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChild" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.findChange(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="findChange" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
</node>

<node TEXT="void setString(String string)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.setString(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setString" MIDDLE_LABEL="setString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setString" MIDDLE_LABEL="setString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setString" MIDDLE_LABEL="setString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="setString" MIDDLE_LABEL="setString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="setString" MIDDLE_LABEL="setString"/>
</node>

<node TEXT="Boolean isHidden()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.isHidden()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.getNextLayerCounts(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;BLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getNextLayerCounts" TARGET_LABEL="isHidden" MIDDLE_LABEL="isHidden"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.wrapChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="wrapChildren" TARGET_LABEL="isHidden" MIDDLE_LABEL="isHidden"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="isHidden" MIDDLE_LABEL="isHidden"/>
</node>

<node TEXT="void setWrapper(MLElementWrapper wrap)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.setWrapper(QMLElementWrapper;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.wrapChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="wrapChildren" TARGET_LABEL="setWrapper" MIDDLE_LABEL="setWrapper"/>
</node>

<node TEXT="MLElementWrapper getWrapper()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getWrapper()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getWrapper" MIDDLE_LABEL="getWrapper"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.addFirstLayer(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)[I" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addFirstLayer" TARGET_LABEL="getWrapper" MIDDLE_LABEL="getWrapper"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getWrapper" MIDDLE_LABEL="getWrapper"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="getWrapper" MIDDLE_LABEL="getWrapper"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="getWrapper" MIDDLE_LABEL="getWrapper"/>
</node>

<node TEXT="HTML toMathML()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.toMathML()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.addFirstLayer(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)[I" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addFirstLayer" TARGET_LABEL="toMathML" MIDDLE_LABEL="toMathML"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="toMathML" MIDDLE_LABEL="toMathML"/>
</node>

<node TEXT="Type getType()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getType()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getType" MIDDLE_LABEL="getType"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.addFirstLayer(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)[I" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addFirstLayer" TARGET_LABEL="getType" MIDDLE_LABEL="getType"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.findNestedMrows(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="findNestedMrows" TARGET_LABEL="getType" MIDDLE_LABEL="getType"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getType" MIDDLE_LABEL="getType"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="getType" MIDDLE_LABEL="getType"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="getType" MIDDLE_LABEL="getType"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getType" MIDDLE_LABEL="getType"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="getType" MIDDLE_LABEL="getType"/>
</node>

<node TEXT="String getTag()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getTag()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.parseTree(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="parseTree" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_Simplify_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="rearrangeNegatives" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignSimpleTypes(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignSimpleTypes" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.checkIsHidden()Ljava/lang/Boolean;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="checkIsHidden" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.addChild(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChild" TARGET_LABEL="getTag" MIDDLE_LABEL="getTag"/>
</node>

<node TEXT="MathMLBindingTree getTree()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.getTree()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Divide;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getTree" MIDDLE_LABEL="getTree"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Add;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getTree" MIDDLE_LABEL="getTree"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/DropController_BothSides_Multiply;.onChange()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onChange" TARGET_LABEL="getTree" MIDDLE_LABEL="getTree"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="getTree" MIDDLE_LABEL="getTree"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;.onDrop(Lcom/allen_sauer/gwt/dnd/client/DragContext;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onDrop" TARGET_LABEL="getTree" MIDDLE_LABEL="getTree"/>
</node>

<node TEXT="Boolean checkIsHidden()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.checkIsHidden()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.add(ILcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="add" TARGET_LABEL="checkIsHidden" MIDDLE_LABEL="checkIsHidden"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.parseTree(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="parseTree" TARGET_LABEL="checkIsHidden" MIDDLE_LABEL="checkIsHidden"/>
</node>

<node TEXT="Boolean isFunction()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.isFunction()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="isFunction" MIDDLE_LABEL="isFunction"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;.checkIsHidden()Ljava/lang/Boolean;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="checkIsHidden" TARGET_LABEL="isFunction" MIDDLE_LABEL="isFunction"/>
</node>

<node TEXT="Term&#xa;Series&#xa;Function&#xa;Exponent&#xa;Fraction&#xa;Variable&#xa;Number" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="HashMap&lt;Node, MathMLBindingNode&gt; nodeMap;&#xa;private MathMLBindingNode prevLeftNode;&#xa;private MathMLBindingNode prevRightNode;&#xa;private MathMLBindingNode prevSibling;&#xa;private MathMLBindingNode curNode;&#xa;MathMLBindingNode nLeft;&#xa;MathMLBindingNode nEq;&#xa;MathMLBindingNode nRight;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void MLtoMLTree(HTML mathMLequation)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.MLtoMLTree(QHTML;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/Boolean;)V|Lcom/sciencegadgets/client/TopNodesNotFoundException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathMLBindingTree" TARGET_LABEL="MLtoMLTree" MIDDLE_LABEL="MLtoMLTree"/>
</node>

<node TEXT="void change(MathMLBindingTree jTree)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.change(QMathMLBindingTree;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/Boolean;)V|Lcom/sciencegadgets/client/TopNodesNotFoundException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathMLBindingTree" TARGET_LABEL="change" MIDDLE_LABEL="change"/>
</node>

<node TEXT="void onRootsFound(Node nodeLeft, Node nodeEquals, Node nodeRight)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.onRootsFound(QNode;QNode;QNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLParser;.(Lcom/google/gwt/user/client/ui/HTML;)V|Lcom/sciencegadgets/client/TopNodesNotFoundException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathMLParser" TARGET_LABEL="onRootsFound" MIDDLE_LABEL="onRootsFound"/>
</node>

<node TEXT="void onVisitNode(Node currentNode, Boolean isLeft, int indexOfChildren)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.onVisitNode(QNode;QBoolean;I)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLParser;.addChildren(Lcom/google/gwt/dom/client/Node;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChildren" TARGET_LABEL="onVisitNode" MIDDLE_LABEL="onVisitNode"/>
</node>

<node TEXT="void onGoingToNextChild(Node currentNode)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLtoMLTree;.onGoingToNextChild(QNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLParser;.addChildren(Lcom/google/gwt/dom/client/Node;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChildren" TARGET_LABEL="onGoingToNextChild" MIDDLE_LABEL="onGoingToNextChild"/>
</node>

<node TEXT="private LinkedList&lt;MathMLBindingNode&gt; nestedMrows = new LinkedList&lt;MathMLBindingNode&gt;();&#xa;private LinkedList&lt;MathMLBindingNode&gt; negatives = new LinkedList&lt;MathMLBindingNode&gt;();" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void change(MathMLBindingTree jTree)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.change(QMathMLBindingTree;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/Boolean;)V|Lcom/sciencegadgets/client/TopNodesNotFoundException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathMLBindingTree" TARGET_LABEL="change" MIDDLE_LABEL="change"/>
</node>

<node TEXT="void parseTree(MathMLBindingNode jNode)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.parseTree(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.change(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="change" TARGET_LABEL="parseTree" MIDDLE_LABEL="parseTree"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.parseTree(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="parseTree" TARGET_LABEL="parseTree" MIDDLE_LABEL="parseTree"/>
</node>

<node TEXT="void assignSimpleTypes(MathMLBindingNode kid)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignSimpleTypes(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.parseTree(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="parseTree" TARGET_LABEL="assignSimpleTypes" MIDDLE_LABEL="assignSimpleTypes"/>
</node>

<node TEXT="void assignComplexChildMrow(MathMLBindingNode kid)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.parseTree(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="parseTree" TARGET_LABEL="assignComplexChildMrow" MIDDLE_LABEL="assignComplexChildMrow"/>
</node>

<node TEXT="void negatePropagate(MathMLBindingNode node)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.negatePropagate(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.assignComplexChildMrow(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assignComplexChildMrow" TARGET_LABEL="negatePropagate" MIDDLE_LABEL="negatePropagate"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.negatePropagate(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="negatePropagate" TARGET_LABEL="negatePropagate" MIDDLE_LABEL="negatePropagate"/>
</node>

<node TEXT="void findNestedMrows(MathMLBindingNode parent, MathMLBindingNode kid)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.findNestedMrows(QMathMLBindingNode;QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.parseTree(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="parseTree" TARGET_LABEL="findNestedMrows" MIDDLE_LABEL="findNestedMrows"/>
</node>

<node TEXT="void rearrangeNestedMrows()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNestedMrows()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.change(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="change" TARGET_LABEL="rearrangeNestedMrows" MIDDLE_LABEL="rearrangeNestedMrows"/>
</node>

<node TEXT="void rearrangeNegatives()" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.rearrangeNegatives()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MLTreeToMathTree;.change(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="change" TARGET_LABEL="rearrangeNegatives" MIDDLE_LABEL="rearrangeNegatives"/>
</node>

<node TEXT="HTML mlHTML = new HTML(&quot;&lt;math&gt;&lt;/math&gt;&quot;);&#xa;Boolean changeDomNodes = false;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void MathTreeToML(MathMLBindingTree sourceTree, Boolean changeDomNodes)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.MathTreeToML(QMathMLBindingTree;QBoolean;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;.toMathML()Lcom/google/gwt/user/client/ui/HTML;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toMathML" TARGET_LABEL="MathTreeToML" MIDDLE_LABEL="MathTreeToML"/>
</node>

<node TEXT="void MathTreeToML(MathMLBindingNode jNode)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.MathTreeToML(QMathMLBindingNode;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void addChild(MathMLBindingNode from, Node to)" ID="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.addChild(QMathMLBindingNode;QNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathTreeToML" TARGET_LABEL="addChild" MIDDLE_LABEL="addChild"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathTreeToML" TARGET_LABEL="addChild" MIDDLE_LABEL="addChild"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathTreeToML;.addChild(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChild" TARGET_LABEL="addChild" MIDDLE_LABEL="addChild"/>
</node>
</node>

<node TEXT="TreeEntry.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="public static final AbsolutePanel mlTree = new AbsolutePanel();&#xa;public static AbsolutePanel apTree = new AbsolutePanel();&#xa;public static ScrollPanel spTree = new ScrollPanel(apTree);" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void onModuleLoad()" ID="Lcom/sciencegadgets/client/equationtree/TreeEntry;.onModuleLoad()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.transport(Lcom/google/gwt/user/client/ui/HTML;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="transport" TARGET_LABEL="onModuleLoad" MIDDLE_LABEL="onModuleLoad"/>
</node>
</node>

<node TEXT="DropControllAssigner.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="void DropControllAssigner(LinkedList&lt;MLElementWrapper&gt; wrappers, Boolean hasJoiner)" ID="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.DropControllAssigner(QLinkedList&lt;QMLElementWrapper;&gt;;QBoolean;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void assign(LinkedList&lt;MLElementWrapper&gt; wrappers, Boolean hasJoiner)" ID="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(QLinkedList&lt;QMLElementWrapper;&gt;;QBoolean;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="DropControllAssigner" TARGET_LABEL="assign" MIDDLE_LABEL="assign"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.selectEquation(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="selectEquation" TARGET_LABEL="assign" MIDDLE_LABEL="assign"/>
</node>

<node TEXT="AbstractMathDropController addDropTarget(MLElementWrapper source, MLElementWrapper target, DropControllAssigner.DropType dropType, Boolean hasJoiner)" ID="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(QMLElementWrapper;QMLElementWrapper;QDropControllAssigner/DropType;QBoolean;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.assign(Ljava/util/LinkedList&lt;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;&gt;;Ljava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="assign" TARGET_LABEL="addDropTarget" MIDDLE_LABEL="addDropTarget"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/DropControllAssigner;.addDropTarget(Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/algebramanipulation/MLElementWrapper;Lcom/sciencegadgets/client/equationtree/DropControllAssigner$DropType;Ljava/lang/Boolean;)Lcom/sciencegadgets/client/algebramanipulation/dropcontrollers/AbstractMathDropController;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addDropTarget" TARGET_LABEL="addDropTarget" MIDDLE_LABEL="addDropTarget"/>
</node>

<node TEXT="Simplify_Add&#xa;Simplify_Multiply&#xa;Simplify_Divide&#xa;BothSides_Add&#xa;BothSides_Multiply&#xa;BothSides_Divide" BACKGROUND_COLOR="#ffa500">
</node>
</node>

<node TEXT="TreeCanvas.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="private MathMLBindingTree mathMLBindingTree;&#xa;private int childSpace;&#xa;private int rowHeight;&#xa;private AbsolutePanel panel;&#xa;private int sideLengthLeft;&#xa;private int sideLengthRight;&#xa;private HashMap&lt;MathMLBindingTree.Type, String&gt; palette;&#xa;private int pad = 5;&#xa;private int topPad = 10;&#xa;// private String nodePicUrl =&#xa;    // &quot;http://www.lanxuan.org/download/openclipart/calloutscloud.svg.SVG&quot;;&#xa;&#xa;    // The number of members in each row&#xa;    private byte[] leftLayerCounts;&#xa;private byte[] rightLayerCounts;&#xa;// These counters aid in placing each member down&#xa;    private byte[] leftCounters;&#xa;private byte[] rightCounters;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void TreeCanvas(AbsolutePanel panel, MathMLBindingTree jTree)" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.TreeCanvas(QAbsolutePanel;QMathMLBindingTree;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/algebramanipulation/EquationTransporter;.selectEquation(Lcom/google/gwt/user/client/ui/HTML;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="selectEquation" TARGET_LABEL="TreeCanvas" MIDDLE_LABEL="TreeCanvas"/>
</node>

<node TEXT="void TreeCanvas(int width, int height, MathMLBindingTree jTree)" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.TreeCanvas(IIQMathMLBindingTree;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.(Lcom/google/gwt/user/client/ui/AbsolutePanel;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="TreeCanvas" TARGET_LABEL="TreeCanvas" MIDDLE_LABEL="TreeCanvas"/>
</node>

<node TEXT="void reDraw()" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.reDraw()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void createPalette()" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.createPalette()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.draw(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="draw" TARGET_LABEL="createPalette" MIDDLE_LABEL="createPalette"/>
</node>

<node TEXT="void draw(MathMLBindingTree jTree)" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.draw(QMathMLBindingTree;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.reDraw()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="reDraw" TARGET_LABEL="draw" MIDDLE_LABEL="draw"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.(Lcom/google/gwt/user/client/ui/AbsolutePanel;Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="TreeCanvas" TARGET_LABEL="draw" MIDDLE_LABEL="draw"/>
</node>

<node TEXT="void drawChildren(MathMLBindingNode pNode, int parentX, int parentY, byte layer, Boolean isLeft)" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(QMathMLBindingNode;IIBQBoolean;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="drawChildren" MIDDLE_LABEL="drawChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.draw(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="draw" TARGET_LABEL="drawChildren" MIDDLE_LABEL="drawChildren"/>
</node>

<node TEXT="void getNextLayerCounts(MathMLBindingNode pNode, byte layer, Boolean isLeft)" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.getNextLayerCounts(QMathMLBindingNode;BQBoolean;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.getNextLayerCounts(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;BLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getNextLayerCounts" TARGET_LABEL="getNextLayerCounts" MIDDLE_LABEL="getNextLayerCounts"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.draw(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="draw" TARGET_LABEL="getNextLayerCounts" MIDDLE_LABEL="getNextLayerCounts"/>
</node>

<node TEXT="int[] addFirstLayer(MathMLBindingTree jTree)" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.addFirstLayer(QMathMLBindingTree;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.draw(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="draw" TARGET_LABEL="addFirstLayer" MIDDLE_LABEL="addFirstLayer"/>
</node>

<node TEXT="void splitSidesInMiddle()" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.splitSidesInMiddle()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.draw(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="draw" TARGET_LABEL="splitSidesInMiddle" MIDDLE_LABEL="splitSidesInMiddle"/>
</node>

<node TEXT="Group createNodeShape(Type type, int x, int y, int width, int height)" ID="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.createNodeShape(QType;IIII)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.addFirstLayer(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree;)[I" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addFirstLayer" TARGET_LABEL="createNodeShape" MIDDLE_LABEL="createNodeShape"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/equationtree/TreeCanvas;.drawChildren(Lcom/sciencegadgets/client/equationtree/MathMLBindingTree$MathMLBindingNode;IIBLjava/lang/Boolean;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="drawChildren" TARGET_LABEL="createNodeShape" MIDDLE_LABEL="createNodeShape"/>
</node>
</node>
</node>

<node TEXT="com.sciencegadgets.client.util" BACKGROUND_COLOR="#eee8aa">

<node TEXT="MathTree.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="MathNode root;&#xa;static int nodeCount = 0;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void MathTree(HTML mathML)" ID="Lcom/sciencegadgets/client/util/MathTree;.MathTree(QHTML;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="Canvas getTreeDrawing()" ID="Lcom/sciencegadgets/client/util/MathTree;.getTreeDrawing()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="Canvas canvas;&#xa;Context2d context;&#xa;//int row = 0;         //current row to draw on&#xa;        int rowCount = 0;     //number of items on the row&#xa;int rowHeight = 100;&#xa;int nodeWidth = 100;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void MathCanvas()" ID="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.MathCanvas()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree;.getTreeDrawing()Lcom/google/gwt/canvas/client/Canvas;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getTreeDrawing" TARGET_LABEL="MathCanvas" MIDDLE_LABEL="MathCanvas"/>
</node>

<node TEXT="Canvas getCanvas()" ID="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.getCanvas()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree;.getTreeDrawing()Lcom/google/gwt/canvas/client/Canvas;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="getTreeDrawing" TARGET_LABEL="getCanvas" MIDDLE_LABEL="getCanvas"/>
</node>

<node TEXT="void Draw()" ID="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.(Lcom/sciencegadgets/client/util/MathTree;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathCanvas" TARGET_LABEL="Draw" MIDDLE_LABEL="Draw"/>
</node>

<node TEXT="void Draw(MathNode node)" ID="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw(QMathNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Draw" TARGET_LABEL="Draw" MIDDLE_LABEL="Draw"/>
</node>

<node TEXT="void Draw(MathNode node, int r, int w)" ID="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw(QMathNode;II)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw(Lcom/sciencegadgets/client/util/MathTree$MathNode;II)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Draw" TARGET_LABEL="Draw" MIDDLE_LABEL="Draw"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw(Lcom/sciencegadgets/client/util/MathTree$MathNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Draw" TARGET_LABEL="Draw" MIDDLE_LABEL="Draw"/>
</node>

<node TEXT="List&lt;MathNode&gt; children;&#xa;//encapsulated dom node &#xa;        Node node;&#xa;String text;&#xa;Type type;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void MathNode(Node n)" ID="Lcom/sciencegadgets/client/util/MathTree$MathNode;.MathNode(QNode;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree;.(Lcom/google/gwt/user/client/ui/HTML;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathTree" TARGET_LABEL="MathNode" MIDDLE_LABEL="MathNode"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathNode;.addChildren()Ljava/util/List&lt;Lcom/sciencegadgets/client/util/MathTree$MathNode;&gt;;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="addChildren" TARGET_LABEL="MathNode" MIDDLE_LABEL="MathNode"/>
</node>

<node TEXT="List&lt;MathNode&gt; addChildren()" ID="Lcom/sciencegadgets/client/util/MathTree$MathNode;.addChildren()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathNode;.(Lcom/sciencegadgets/client/util/MathTree;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathNode" TARGET_LABEL="addChildren" MIDDLE_LABEL="addChildren"/>
</node>

<node TEXT="List&lt;MathNode&gt; getChildren()" ID="Lcom/sciencegadgets/client/util/MathTree$MathNode;.getChildren()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw(Lcom/sciencegadgets/client/util/MathTree$MathNode;II)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Draw" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw(Lcom/sciencegadgets/client/util/MathTree$MathNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Draw" TARGET_LABEL="getChildren" MIDDLE_LABEL="getChildren"/>
</node>

<node TEXT="String getNodeText()" ID="Lcom/sciencegadgets/client/util/MathTree$MathNode;.getNodeText()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathNode;.(Lcom/sciencegadgets/client/util/MathTree;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathNode" TARGET_LABEL="getNodeText" MIDDLE_LABEL="getNodeText"/>
</node>

<node TEXT="Type determineType()" ID="Lcom/sciencegadgets/client/util/MathTree$MathNode;.determineType()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathNode;.(Lcom/sciencegadgets/client/util/MathTree;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathNode" TARGET_LABEL="determineType" MIDDLE_LABEL="determineType"/>
</node>

<node TEXT="String toString()" ID="Lcom/sciencegadgets/client/util/MathTree$MathNode;.toString()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw(Lcom/sciencegadgets/client/util/MathTree$MathNode;II)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Draw" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/storage/client/StorageMap;.get(Ljava/lang/Object;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="get" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/MethodDispatch;.invoke(Lcom/google/gwt/dev/shell/JsValue;[Lcom/google/gwt/dev/shell/JsValue;Lcom/google/gwt/dev/shell/JsValue;)Z" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="invoke" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/log/SwingLoggerPanel;.treeLogTraverse(Ljava/lang/StringBuilder;Ljavax/swing/tree/TreeNode;I)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="treeLogTraverse" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/javac/BytecodeSignatureMaker$CompileDependencyVisitor;.visitField(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)Lcom/google/gwt/dev/asm/FieldVisitor;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="visitField" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/validation/client/BaseMessageInterpolator$2154;.apply(Ljava/lang/String;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="apply" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/rpc/server/CommandSerializationUtil$9788;.set(Ljava/lang/Object;JLjava/lang/Object;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="set" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/js/rhino/JavaScriptException;.(Ljava/lang/Object;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="JavaScriptException" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/log/SwingLoggerPanel;.valueChanged(Ljavax/swing/event/TreeSelectionEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="valueChanged" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathNode;.(Lcom/sciencegadgets/client/util/MathTree;Lcom/google/gwt/dom/client/Node;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="MathNode" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/BrowserChannelServer;.invokeJavascript(Lcom/google/gwt/dev/shell/CompilingClassLoader;Lcom/google/gwt/dev/shell/JsValueOOPHM;Ljava/lang/String;[Lcom/google/gwt/dev/shell/JsValueOOPHM;Lcom/google/gwt/dev/shell/JsValueOOPHM;)V|Ljava/lang/Throwable;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="invokeJavascript" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/util/JsniRef;.equals(Ljava/lang/Object;)Z" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="equals" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/asm/util/AbstractVisitor;.printList(Ljava/io/PrintWriter;Ljava/util/List;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="printList" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/storage/client/StorageMap;.containsKey(Ljava/lang/Object;)Z" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="containsKey" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/shell/tomcat/CommonsLoggerAdapter;.doLog(Lcom/google/gwt/core/ext/TreeLogger$Type;Ljava/lang/Object;Ljava/lang/Throwable;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="doLog" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/server/rpc/RPCRequest;.toString()Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toString" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/storage/client/StorageMap;.remove(Ljava/lang/Object;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="remove" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/web/bindery/requestfactory/server/Resolver$ResolutionKey;.toString()Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toString" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/storage/client/StorageMap$StorageEntrySet;.remove(Ljava/lang/Object;)Z" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="remove" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/rebind/AbstractMethodCreator;.println(Ljava/lang/Object;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="println" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/jjs/impl/GenerateJavaAST$JavaASTGenerationVisitor;.translateException(Ljava/lang/Object;Ljava/lang/Throwable;)Lcom/google/gwt/dev/jjs/InternalCompilerException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="translateException" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/benchmarks/BenchmarkReport$BenchmarkXml;.toElement(Lorg/w3c/dom/Document;Lcom/google/gwt/benchmarks/client/impl/Trial;)Lorg/w3c/dom/Element;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toElement" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/js/rhino/Node;.toString()Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="toString" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/gwt/dev/util/msg/FormatterToString;.format(Ljava/lang/Object;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="format" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/google/web/bindery/autobean/vm/impl/JsonSplittable;.makeSplittable(Ljava/lang/Object;)Lcom/google/web/bindery/autobean/vm/impl/JsonSplittable;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="makeSplittable" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/MathTree$MathCanvas;.Draw(Lcom/sciencegadgets/client/util/MathTree$MathNode;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Draw" TARGET_LABEL="toString" MIDDLE_LABEL="toString"/>
</node>

<node TEXT="mrow&#xa;//term&#xa;        mi&#xa;//variable&#xa;        mo&#xa;//operator&#xa;        msub&#xa;//variable with subscript&#xa;        mn&#xa;//subscript&#xa;        notype&#xa;//error&#xa;        leaf" BACKGROUND_COLOR="#ffa500">
</node>
</node>
</node>

<node TEXT="com.sciencegadgets.client.util.equationwriter" BACKGROUND_COLOR="#eee8aa">

<node TEXT="EquationKeyboard.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="FlexTable flexTable = new FlexTable();&#xa;Widget source;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void EquationKeyboard(Widget w)" ID="Lcom/sciencegadgets/client/util/equationwriter/EquationKeyboard;.EquationKeyboard(QWidget;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/Symbol$3677;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="EquationKeyboard" MIDDLE_LABEL="EquationKeyboard"/>
</node>

<node TEXT="void onLoad()" ID="Lcom/sciencegadgets/client/util/equationwriter/EquationKeyboard;.onLoad()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/Widget;.onAttach()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onAttach" TARGET_LABEL="onLoad" MIDDLE_LABEL="onLoad"/>
</node>

<node TEXT="void KeyboardButton(String u)" ID="Lcom/sciencegadgets/client/util/equationwriter/EquationKeyboard$KeyboardButton;.KeyboardButton(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/EquationKeyboard;.onLoad()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onLoad" TARGET_LABEL="KeyboardButton" MIDDLE_LABEL="KeyboardButton"/>
</node>

<node TEXT="void setOnClick()" ID="Lcom/sciencegadgets/client/util/equationwriter/EquationKeyboard$KeyboardButton;.setOnClick()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.(Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Symbol" TARGET_LABEL="setOnClick" MIDDLE_LABEL="setOnClick"/>
</node>
</node>

<node TEXT="Symbol.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="Label label;&#xa;Image image;&#xa;String utf8;&#xa;int size;&#xa;boolean isLoaded = false;&#xa;VerticalPanel left = new VerticalPanel();&#xa;VerticalPanel right = new VerticalPanel();&#xa;HorizontalPanel top = new HorizontalPanel();&#xa;HorizontalPanel bottom = new HorizontalPanel();&#xa;ClickHandler invokeContextMenu;&#xa;ClickHandler invokeKeyboard = new ClickHandler(){&#xa;        public void onClick(ClickEvent event){&#xa;            event.stopPropagation();&#xa;            Widget w = (Widget) event.getSource();&#xa;            w.getElement().getStyle().setBackgroundColor(&quot;gray&quot;);&#xa;            RootPanel.get().add(new EquationKeyboard(w));&#xa;        }&#xa;    };" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void Symbol(String u)" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.Symbol(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/EquationKeyboard$KeyboardButton;.(Lcom/sciencegadgets/client/util/equationwriter/EquationKeyboard;Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="KeyboardButton" TARGET_LABEL="Symbol" MIDDLE_LABEL="Symbol"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/EquationWriter;.onLoad()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onLoad" TARGET_LABEL="Symbol" MIDDLE_LABEL="Symbol"/>
</node>

<node TEXT="void onLoad()" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.onLoad()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/Widget;.onAttach()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onAttach" TARGET_LABEL="onLoad" MIDDLE_LABEL="onLoad"/>
</node>

<node TEXT="void setSize(int w, int h)" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.setSize(II)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void setFont(int s)" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.setFont(I)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.setSubScript(Lcom/sciencegadgets/client/util/equationwriter/Symbol;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="setSubScript" TARGET_LABEL="setFont" MIDDLE_LABEL="setFont"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.setSuperScript(Lcom/sciencegadgets/client/util/equationwriter/Symbol;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="setSuperScript" TARGET_LABEL="setFont" MIDDLE_LABEL="setFont"/>
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.(Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Symbol" TARGET_LABEL="setFont" MIDDLE_LABEL="setFont"/>
</node>

<node TEXT="int getHeight()" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.getHeight()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="int getWidth()" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.getWidth()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="Symbol getSuperScript()" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.getSuperScript()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/EquationWriter$1720;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="getSuperScript" MIDDLE_LABEL="getSuperScript"/>
</node>

<node TEXT="void setSuperScript(Symbol s)" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.setSuperScript(QSymbol;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/EquationWriter$1720;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="setSuperScript" MIDDLE_LABEL="setSuperScript"/>
</node>

<node TEXT="void setSubScript(Symbol s)" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.setSubScript(QSymbol;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/EquationWriter$1720;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="setSubScript" MIDDLE_LABEL="setSubScript"/>
</node>

<node TEXT="void setOnClick()" ID="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.setOnClick()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/Symbol;.(Ljava/lang/String;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="Symbol" TARGET_LABEL="setOnClick" MIDDLE_LABEL="setOnClick"/>
</node>
</node>

<node TEXT="EquationWriter.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="EquationWriter panel;&#xa;int count = 0;&#xa;Button goButton;&#xa;String url = &quot;integral.png&quot;;&#xa;String x = &quot;x.png&quot;;&#xa;Symbol integral1;&#xa;Symbol integral2;&#xa;Symbol integral3;&#xa;Symbol integral4;&#xa;ClickHandler goClick = new ClickHandler(){&#xa;        public void onClick(ClickEvent event){&#xa;            panel.add(integral1);&#xa;            integral1.setSuperScript(integral2);&#xa;            integral1.getSuperScript().setSuperScript(integral3);&#xa;            integral1.setSubScript(integral4);&#xa;            //integral2.setSuperScript(integral3);&#xa;        }&#xa;    };" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void EquationWriter()" ID="Lcom/sciencegadgets/client/util/equationwriter/EquationWriter;.EquationWriter()V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="void onLoad()" ID="Lcom/sciencegadgets/client/util/equationwriter/EquationWriter;.onLoad()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/Widget;.onAttach()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onAttach" TARGET_LABEL="onLoad" MIDDLE_LABEL="onLoad"/>
</node>

<node TEXT="void addTo()" ID="Lcom/sciencegadgets/client/util/equationwriter/EquationWriter;.addTo()V" BACKGROUND_COLOR="#ffa07a">
</node>
</node>

<node TEXT="ContextMenu.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="VerticalPanel content = new VerticalPanel();&#xa;Widget clickSource;" BACKGROUND_COLOR="#ffa500">
</node>

<node TEXT="void ContextMenu(ClickEvent event)" ID="Lcom/sciencegadgets/client/util/equationwriter/ContextMenu;.ContextMenu(QClickEvent;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/Symbol$3402;.onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="onClick" TARGET_LABEL="ContextMenu" MIDDLE_LABEL="ContextMenu"/>
</node>

<node TEXT="void hide(boolean autoClosed)" ID="Lcom/sciencegadgets/client/util/equationwriter/ContextMenu;.hide(Z)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/PopupPanel;.hide()V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="hide" TARGET_LABEL="hide" MIDDLE_LABEL="hide"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/MenuBar;.close(Z)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="close" TARGET_LABEL="hide" MIDDLE_LABEL="hide"/>
<arrowlink DESTINATION="Lcom/google/gwt/user/client/ui/PopupPanel;.previewNativeEvent(Lcom/google/gwt/user/client/Event$NativePreviewEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="previewNativeEvent" TARGET_LABEL="hide" MIDDLE_LABEL="hide"/>
</node>

<node TEXT="void setOptions()" ID="Lcom/sciencegadgets/client/util/equationwriter/ContextMenu;.setOptions()V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/client/util/equationwriter/ContextMenu;.(Lcom/google/gwt/event/dom/client/ClickEvent;)V" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="ContextMenu" TARGET_LABEL="setOptions" MIDDLE_LABEL="setOptions"/>
</node>
</node>
</node>

<node TEXT="com.sciencegadgets.server" BACKGROUND_COLOR="#eee8aa">

<node TEXT="GreetingServiceImpl.java" BACKGROUND_COLOR="#ff00ff">

<node TEXT="String greetServer(String input)" ID="Lcom/sciencegadgets/server/GreetingServiceImpl;.greetServer(QString;)V" BACKGROUND_COLOR="#ffa07a">
</node>

<node TEXT="String stringToMathML(String strEval)" ID="Lcom/sciencegadgets/server/GreetingServiceImpl;.stringToMathML(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/server/GreetingServiceImpl;.greetServer(Ljava/lang/String;)Ljava/lang/String;|Ljava/lang/IllegalArgumentException;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="greetServer" TARGET_LABEL="stringToMathML" MIDDLE_LABEL="stringToMathML"/>
</node>

<node TEXT="String randomize(String b4rand)" ID="Lcom/sciencegadgets/server/GreetingServiceImpl;.randomize(QString;)V" BACKGROUND_COLOR="#ffa07a">
<arrowlink DESTINATION="Lcom/sciencegadgets/server/GreetingServiceImpl;.stringToMathML(Ljava/lang/String;)Ljava/lang/String;" STARTARROW="DEFAULT" ENDARROW="NONE" SOURCE_LABEL="stringToMathML" TARGET_LABEL="randomize" MIDDLE_LABEL="randomize"/>
</node>

<node TEXT="String openDatabase()" ID="Lcom/sciencegadgets/server/GreetingServiceImpl;.openDatabase()V" BACKGROUND_COLOR="#ffa07a">
</node>
</node>
</node>
</node>
</map>