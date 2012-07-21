/**
 * GraphAPI implements a typical graph API, except that this graph API is persisted using InterSystems 
 * globals database.  Please see the class diagram for a visual overview.
 * 
 * <h1> Interfaces </h1>
 * 
 * The interfaces for <b>graphapi</b> are the basics, implemented as <i>generics</i>, as follows:
 * 
 * <ul>
 * <li> Node  - some Object we want to graph </li>
 * <li> Graph - which contains the Nodes </li>
 * <li> Edge - connects two Nodes in the context of the Graph </li>
 * </ul>
 * 
 * 
 *
 * <h1> Requirements and Objectives </h1>
 * 
 * <ul>
 * <li> present as simple a Graph interface as possible </li>
 * <li> allow but not require a Graph be persisted in GlobalsDB </li>
 * <li> allow Nodes to participate independently in multiple Graphs  </li>
 * <li> allow the use of any Java class as a graph node</li>
 * <li> support directed graphs   </li>
 * <li> support weighted edges   </li>
 * <li> support subgraphs with reference to 'supergraphs' </li>
 * <li> implement cycle detection  </li>
 * <li> implement shortest-path calculation </li>
 * <li> support direct rendering by graphviz </li>
 * </ul>
 * 
 * 
 */

package com.intersys.globals.graphapi;

