Version:	0.4.09.01    
Author:		Douglas Cantrell    
Contact:	cantrell.douglas@gmail.com

================================================================================================================================
This program is meant to simplify the process of generating Yee diagrams, and similar graphs, for the benefit of those with a general interest in voting systems. Additional features and improved implementation are things I intend to work on eventually, but probably not in the near future. Bug fixes will have higher priority.

If you're reading this because you want to add a voting method, the steps are:    
1) Create a class which implements I_VotingMethod.    
2) Add the name of the method to METHOD_NAMES in ControlGraphPanel.    
3) Add the index of the string you just added as a constant in ControlGraphPanel.    
4) Add the constant you just created to the switch statement in ControlGraphPresenter.    

If you want to skip steps three and four, you can just rewrite M_DefaultMethod.

================================================================================================================================
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
