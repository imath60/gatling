/**
 * Copyright 2011-2013 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.core.scenario

import akka.actor.ActorRef
import io.gatling.core.action.system
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper.zeroMs
import io.gatling.core.controller.inject.InjectionProfile

case class Scenario(name: String, entryPoint: ActorRef, injectionProfile: InjectionProfile) {

	def run(runUUID: String, offset: Int) {
		import system.dispatcher

		def newSession(i: Int) = Session(name, runUUID + (i + offset))

		injectionProfile.allUsers.zipWithIndex.foreach {
			case (startingTime, index) =>
				if (startingTime == zeroMs)
					entryPoint ! newSession(index)
				else
					system.scheduler.scheduleOnce(startingTime, entryPoint, newSession(index))
		}
	}
}