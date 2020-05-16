import {History} from './history';

const world = '🗺️';
const history: History = {from: 'Home'};

export function hello(word: string = world): string {
  console.log(`Hello ${history.from}! `);
  return `Hello ${world}! `;
}
