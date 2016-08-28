/*
 * Charlatan is a premium CS:GO cheat ran on the JVM.
 * Copyright (C) 2016 Thomas Nappo, Jonathan Beaudoin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.charlatano.game

import com.charlatano.game.netvars.NetVars
import com.charlatano.game.offsets.EngineOffsets.dwClientState
import com.charlatano.game.offsets.EngineOffsets.dwInGame
import com.charlatano.utils.retry
import com.charlatano.utils.uint
import org.jire.arrowhead.Module
import org.jire.arrowhead.Process
import org.jire.arrowhead.processByName

object CSGO {
	
	lateinit var csgoEXE: Process
		private set
	
	lateinit var clientDLL: Module
		private set
	lateinit var engineDLL: Module
		private set
	
	fun initalize() {
		retry(10) { csgoEXE = processByName("csgo.exe")!! }
		retry(10) {
			csgoEXE.loadModules()
			
			clientDLL = csgoEXE.modules["client.dll"]!!
			engineDLL = csgoEXE.modules["engine.dll"]!!
		}
		
		// TODO: Offsets.load()
		NetVars.load()
		
		retry(5) {
			val enginePointer = engineDLL.uint(dwClientState)
			val inGame = csgoEXE.int(enginePointer + dwInGame) == 6
			if (!inGame) RuntimeException() //TODO find nicer solution
		}
	}
	
	const val ENTITY_SIZE = 16
	const val GLOW_OBJECT_SIZE = 56
	
}