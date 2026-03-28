import { defineStore } from 'pinia'
import { agentApi } from '@/api'

export const useAgentStore = defineStore('agent', {
  state: () => ({
    agents: [],
    currentAgentId: null,
    currentAgent: null
  }),

  getters: {
    currentAgentName(state) {
      const agent = state.agents.find(a => a.id === state.currentAgentId)
      return agent?.name || ''
    }
  },

  actions: {
    async fetchAgents() {
      try {
        this.agents = await agentApi.getAll()
      } catch (error) {
        console.error('获取Agent列表失败:', error)
        this.agents = []
      }
    },

    setCurrentAgentId(id) {
      this.currentAgentId = id
      this.currentAgent = this.agents.find(a => a.id === id) || null
    },

    getCurrentAgentInfo() {
      if (!this.currentAgentId) return null
      return {
        id: this.currentAgentId,
        name: this.currentAgentName
      }
    }
  }
})